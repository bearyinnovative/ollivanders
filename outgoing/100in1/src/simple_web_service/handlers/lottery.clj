(ns simple-web-service.handlers.lottery
  (:require [simple-web-service.utils :as u :refer [success]]
            [simple-web-service.redis :as redis])
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

(defn- parse-params
  [req]
  (println req)
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        username (:user_name params)
        channel-name (:channel_name params)
        content-str (-> (cstr/replace text trigger-word "")
                      cstr/trim)]
    (concat [username channel-name] (cstr/split content-str #" " 2))))

(defmulti process-lottery (fn [action params] action))

(defmethod process-lottery "start"
  [_ [username & _]]
  (if (= "vivian" username)
    (do
      (reset! started true)
      (success {:text "抽奖活动正式开始了，大家可以通过 /lottery gopher 参与抽奖"}))
    (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"})))

(defmethod process-lottery "end"
  [_ [username & -]]
  (if (= "vivian" username)
    (let [users (list-users)]
      (loop [ret #{}]
        (let [u (rand-nth users)
              ret (cons u ret)]
          (if (>= (count ret) 3)
            (success {:text (format "最终获奖名单:\r\n%s" (cstr/join "\r\n" ret))})
            (recur ret)))))
    (success {:text "你是黑客哦，只有首席妹子才可以用这个命令"})))

(defmethod process-lottery "gopher"
  [_ [username & _]]
  (if @started
    (if (> (add-user username) 0)
      (success {:text "添加成功，可以通过 list 命令来查看"})
      (success {:text "已经添加过了，可以通过 list 命令来查看"}))
    (success {:text "抽奖活动还没有开始，请稍等片刻"})))

(defmethod process-lottery "add"
  [action [username channel-name args]]
  (let [users (cstr/split args #" ")]
    (if (> (apply add-user username) 0)
      (success {:text "添加成功，可以通过 list 命令来查看"})
      (success {:text "已经添加过了，可以通过 list 命令来查看"}))))

(defmethod process-lottery "del"
  [action [username channel-name args]]
  (let [users (cstr/split args #" ")]
    (if (> (apply del-user users) 0)
      (success {:text "删除成功，可以通过 list 命令来查看"})
      (success {:text "已经删除过了，可以通过 list 命令来查看"}))))

(defmethod process-lottery "list"
  [action [username channel-name args]]
  (let [users (list-users)]
    (success {:text (format "总计有 %s 人: \r\n %s" (count users) (cstr/join "\r\n" users))})))

(defn- error-response
  []
  (success {:text "你是黑客，格式不对哦"}))

(defn lottery
  [req]
  (let [[username channel-name action args] (parse-params req)]
    (println username channel-name action args)
    (if action
      (process-lottery action [username channel-name args])
      (error-response))))

