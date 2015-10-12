(ns simple-web-service.handlers.lottery
  (:require [simple-web-service.utils :as u :refer [success]]
            [simple-web-service.redis :as redis]
            [simple-web-service.handlers.helper :refer :all])
  (:require [clojure.string :as cstr]))

(def started (atom false))

(defonce lottery-set-key "lottery:set")

(defn add-user
  ([name]
   (redis/sadd lottery-set-key name))
  ([name & more]
   (apply redis/sadd lottery-set-key name more)))

(defn del-user
  ([name]
   (redis/srem lottery-set-key name))
  ([name & more]
   (apply redis/srem lottery-set-key name more)))

(defn list-users
  []
  (redis/smembers lottery-set-key))

(defmulti process-lottery (fn [action params] action))

(defmethod process-lottery "start"
  [_ [username & _]]
  (if (= "vivian" username)
    (do
      (reset! started true)
      (success {:text "抽奖活动正式开始了，大家可以通过 /lottery gopher 参与抽奖"}))
    (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"})))

(defn- get-winners-from-users
  [users winner-cnt]
  (if (<= (count users) winner-cnt)
    users
    (loop [ret #{}]
      (let [u (rand-nth users)
            ret (conj ret u)]
        (if (>= (count ret) winner-cnt)
          ret
          (recur ret))))))

(defmethod process-lottery "end"
  [_ [username & -]]
  (let [winner-cnt 3]
    (if @started
      (if (= "vivian" username)
        (do
          (reset! started false)
          (let [winners (get-winners-from-users (list-users) winner-cnt)]
            (success {:text (format "最终获奖名单:\r\n%s" (cstr/join "\r\n" winners))})))
        (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"}))
      (success {:text "抽奖活动还没有开始，请稍等片刻"}))))

(defmethod process-lottery "reset"
  [_ [username & _]]
  (if (not @started)
    (if (= "vivian" username)
      (do
        (redis/del lottery-set-key)
        (success {:text "已经清空候选人, 可以通过 /lottery list 查看"}))
      (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"}))
    (success {:text "抽奖活动已经开始了，无法清空候选人"})))

(defn helper
  [username]
  (if (= "vivian" username)
    (success {:text "/lottery start 开始抽奖环节\r\n/lottery gopher 报名加入抽奖\r\n/lottery add [username&] 手动添加某人加入抽奖环节\r\n/lottery del [username&] 手动删除某人加入抽奖环节\r\n/lottery list 看现在抽奖名单\r\n/lottery end 结束报名环节，并抽出 3 名获奖同学\r\n/lottery help 显示帮助菜单"})
    (success {:text "/lottery gopher 报名加入抽奖\r\n/lottery list 看现在抽奖名单\r\n/lottery help 显示帮助菜单"})))

(defmethod process-lottery "help"
  [_ [username & _]]
  (helper username))

(defmethod process-lottery "gopher"
  [_ [username & _]]
  (if @started
    (if (> (add-user username) 0)
      (success {:text "添加成功，可以通过 list 命令来查看"})
      (success {:text "已经添加过了，可以通过 list 命令来查看"}))
    (success {:text "抽奖活动还没有开始，请稍等片刻"})))

(defmethod process-lottery "add"
  [action [username channel-name args]]
  (if (= "vivian" username)
    (if (and args (not (cstr/blank? args)))
      (let [users (cstr/split args #" ")]
        (if (> (apply add-user users) 0)
          (success {:text "添加成功，可以通过 list 命令来查看"})
          (success {:text "已经添加过了，可以通过 list 命令来查看"})))
      (success {:text "没有输入要加入的用户名, 比如 /lottery add loddit"}))
    (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"})))

(defmethod process-lottery "del"
  [action [username channel-name args]]
  (if (= "vivian" username)
    (if (and args (not (cstr/blank? args)))
      (let [users (cstr/split args #" ")]
        (if (> (apply del-user users) 0)
          (success {:text "删除成功，可以通过 list 命令来查看"})
          (success {:text "已经删除过了，可以通过 list 命令来查看"})))
      (success {:text "没有输入要删除的用户名, 比如 /lottery del loddit"}))
    (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"})))

(defmethod process-lottery "list"
  [action [username channel-name args]]
  (let [users (list-users)]
    (success {:text (format "总计有 %s 人: \r\n %s" (count users) (cstr/join "\r\n" users))})))

(defmethod process-lottery :default
  [action [username & _]]
  (println (format  "error action: %s" action))
  (helper username))

(defn lottery
  [req]
  (let [[username channel-name subdomain action args] (parse-outgoing-params req)]
    (if (and action (not (cstr/blank? action)))
      (process-lottery action [username channel-name args])
      (error-response))))

