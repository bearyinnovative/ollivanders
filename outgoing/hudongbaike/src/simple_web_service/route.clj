(ns simple-web-service.route
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-http.client :as http]
            [clojure.data.json :as json])
  (:import [org.jsoup Jsoup]))

(defn response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json;charset=utf-8"
             "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
   :body (json/write-str data)})

(defn success
  ([& [result]]
     (response result)))

(defn- json-request?
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

(defn gen-wiki-url
  [keyword]
  (str "http://www.baike.com/wiki/" keyword))

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

(defn get-from-wiki
  [keyword]
  (let [url (gen-wiki-url keyword)
        resp (get-resource url)]
    (when (= 200 (-> resp :status))
      (let [body (:body resp)
            doc (Jsoup/parse body url)
            fp (-> (.body doc)
                 (.select "#unifyprompt div p")
                 first)]
        (.text fp)))))

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

(defroutes app-routes
  (POST "/wiki" [] (fn [req]
                    (println req)
                    (let [params (:params req)
                          text (:text params)
                          trigger-word (:trigger_word params)
                          to-search (clojure.string/trim
                                     (clojure.string/replace text trigger-word ""))]
                      (success {:text (get-from-wiki to-search)})))))

(def app (handler/site (-> app-routes
                         wrap-json-params)))
