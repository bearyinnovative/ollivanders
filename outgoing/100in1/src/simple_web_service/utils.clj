(ns simple-web-service.utils
  (:require [clojure.data.json :as json]
            [clj-http.client :as http]
            [ring.util.codec :as codec]
            [clojure.string :as str]))

(defn response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json;charset=utf-8"
             "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
   :body (json/write-str data)})

(defn success
  ([& [result]]
   (response result)))

(defn json-request?
  [req]
  (if-let [#^String type (:content-type req)]
    (not (empty? (re-find #"^application/(vnd.+)?json" type)))))

(defn illegal-json [json-str]
  {:status 400
   :headers {"Content-Type" "text/plain"}
   :body (str "illegal json format: " json-str)})

(defn wrap-json-params [handler]
  (fn [req]
    (if-let [body (and (json-request? req) (:body req))]
      (let [bstr (slurp body)]
        (if (not-empty bstr)
          (if-let [json-params (try
                                 (json/read-str bstr :key-fn keyword)
                                 (catch Exception _
                                   nil))]
            (if (map? json-params) ; (= (clojure.data.json/read-str "\"a\"") "a")
              (let [req* (assoc req
                                :json-params json-params
                                :params (merge (:params req) json-params))]
                (handler req*))
              (illegal-json bstr))
            (illegal-json bstr))
          (handler req)))
      (handler req))))

(defonce headers
  {"accept" "text/html"
   "user-agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.101 Safari/537.36"})

(defn get-resource [url]
  (try
    (http/get url
              {:headers headers
               :socket-timeout 10000 ; 2 sec
               :conn-timeout 10000 ; 2 sec to timeout
               :max-redirects 3
               :throw-exceptions false})
    (catch Exception e
      (println (format "exception '%s' while getting '%s'"
                       (.getMessage e)
                       url))
      nil)))

(defn str-length [string]
  "双字节字符占2位"
  (-> string
    (clojure.string/replace #"[^\x00-\xff]" "**")
    (count)))

(defn ->int
  ([s] (->int s nil))
  ([s default]
   (try
     (cond
       (string? s) (Integer/parseInt s)
       (instance? Number s) (.intValue s)
       :else default)
     (catch Exception e
       default))))

(defn encode-url [query-string]
  (when query-string
    (-> query-string
        codec/url-encode
        (str/replace #"\." "%2E")
        (str/replace #"\+" "%2B"))))
