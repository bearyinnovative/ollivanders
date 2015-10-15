(ns simple-web-service.handlers.helper
  (:require [clojure.string :as cstr])
  (:require [simple-web-service.utils :refer [success]]))

(defn parse-outgoing-params
  [req]
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        username (:user_name params)
        subdomain (:subdomain params)
        channel-name (:channel_name params)
        [action args] (-> (cstr/replace text trigger-word "")
                        cstr/trim
                        (cstr/split #" " 2))]
    [trigger-word username subdomain channel-name action args]))

(defn error-response
  ([]
   (error-response "你是黑客，格式不对哦"))
  ([msg]
   (success {:text msg})))
