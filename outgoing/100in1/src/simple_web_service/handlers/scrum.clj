(ns simple-web-service.handlers.scrum
  (:require [clojure.string :as cstr]
            [clj-time.core :as time-core]
            [clj-time.format :as time-fmt])
  (:require [simple-web-service.utils :as u :refer [success]]
            [simple-web-service.handlers.helper :refer :all]
            [simple-web-service.redis :as redis]))

(def o (Object.))

(defonce admin-list
  #{"tangxm"
    "aaa"})

(defn- gen-team-report-key
  [subdomain]
  (str "report:" subdomain ":content"))

(defn- gen-team-member-key
  [subdomain]
  (str "report:" subdomain ":members"))

(defn- is-admin?
  [username]
  (admin-list username))

(defn- date-time-str
  [ldt]
  (time-fmt/unparse-local-date (time-fmt/formatter-local "yyyy-MM-dd") ldt))

(defn- get-today-str
  []
  (date-time-str (time-core/today)))

(defn- gen-report
  [report-data]
  (let [today-str (get-today-str)
        content (reduce
                 (fn [content [username tasks]]
                   (if (cstr/blank? content)
                     (str username ": " tasks)
                     (str content "\n"
                          username ": " tasks)))
                 ""
                 report-data)]
    (str today-str " 晨会摘要: \n"
         "------------------------------\n"
         content)))

(defn- get-members
  [args]
  (map
   #(-> (cstr/replace % #"@" "")
      (cstr/trim))
   (cstr/split args #" ")))

(defmulti process-scrum (fn [action params] action))

(defmethod process-scrum "s"
  [action [username subdomain channel-name args]]
  (if (is-admin? username)
    (success {:text "请大家在 #bearybot 对话里面写一下昨天的工作, /scrum r {这里填写工作内容, 一行}"})
    (error-response (str "只有管理员才可以操作"))))

(defmethod process-scrum "end"
  [action [username subdomain channel-name args]]
  (if (is-admin? username)
    (let [today-str (get-today-str)
          team-report-key (gen-team-report-key subdomain)
          content (gen-report
                   (redis/hget team-report-key today-str))]
      (success {:text content}))
    (error-response (str "只有管理员才可以操作"))))

(defmethod process-scrum "ma"
  [action [username subdomain channel-name args]]
  (if (is-admin? username)
    (let [team-member-key (gen-team-member-key subdomain)]
      (apply redis/sadd team-member-key (get-members args))
      (success {:text "添加成员成功"}))
    (error-response (str "只有管理员才可以操作"))))

(defmethod process-scrum "ml"
  [action [username subdomain channel-name args]]
  (if (is-admin? username)
    (let [team-member-key (gen-team-member-key subdomain)
          members (redis/smembers team-member-key)]
      (success {:text (str "成员列表:\n"
                           (cstr/join "\n" members))}))
    (error-response (str "只有管理员才可以操作"))))

(defmethod process-scrum "r"
  [action [username subdomain channel-name args]]
  (if (nil? channel-name)
    (locking o
      (let [today-str (get-today-str)
            team-report-key (gen-team-report-key subdomain)
            report-today (or (redis/hget
                              team-report-key
                              today-str)
                             {})]
        (redis/hset team-report-key today-str
                    (assoc report-today username args))
        (success {:text (str "记录成功，内容:\n"
                             username ": " args)})))
    (error-response "只能在 #bearybot 里面进行晨会记录")))

(defmethod process-scrum "help"
  [action [username subdomain channel-name args]]
  (let [common-parts (str "/scrum r {I did...} -- 写晨报;\n"
                          "/scrum help         -- 帮助文档;\n")]
    (if (is-admin? username)
      (success {:text (str "/scrum s            -- 开始晨报流程;\n"
                           "/scrum e            -- 总结晨报;\n"
                           "/scrum ma {a b ...} -- 加入成员;\n"
                           "/scrum md {a b ...} -- 移除成员;\n"
                           "/scrum ml           -- 显示成员列表;\n"
                           common-parts)})
      (success {:text common-parts}))))

(defn scrum
  [req]
  (let [[username subdomain channel-name action args] (parse-outgoing-params req)]
    (if (and action (not (cstr/blank? action)))
      (process-scrum action [username subdomain channel-name args])
      (error-response))))
