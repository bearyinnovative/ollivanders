(ns simple-web-service.handlers.wiki
  (:require [simple-web-service.utils :as u :refer [success]])
  (:import [org.jsoup Jsoup]))

(defn gen-wiki-url
  [keyword]
  (str "http://www.baike.com/wiki/" keyword))

(defn get-from-wiki
  [keyword]
  (let [url (gen-wiki-url keyword)
        resp (u/get-resource url)]
    (when (= 200 (-> resp :status))
      (let [body (:body resp)
            doc (Jsoup/parse body url)
            fp (-> (.body doc)
                 (.select "#unifyprompt div p")
                 first)]
        (.text fp)))))

(defn gen-en-wiki-url [keyword]
  (str "http://en.wikipedia.org/wiki/" keyword))

(defn get-from-en-wiki
  [keyword]
  (let [url (gen-en-wiki-url keyword)
        resp (u/get-resource url)]
    (when (= 200 (-> resp :status))
      (let [body (:body resp)
            doc (Jsoup/parse body url)
            fp (-> (.body doc)
                 (.select "#bodyContent p")
                 first)]
        (.text fp)))))

(defn format-field
  [s max-length patch]
  (let [trimed-s (clojure.string/replace s #" " "")]
    (if (< (u/str-length trimed-s) max-length)
      (str
       trimed-s
       (clojure.string/join (repeat (quot (- max-length (u/str-length trimed-s)) (u/str-length patch)) patch)))
      trimed-s)))

(defn get-from-mh-wiki
  []
  (let [url "http://www.dopr.net/formonsterhunter/%E3%80%90MH4G%E3%80%91%E9%BB%92%E8%BD%9F%E7%AB%9C%E3%83%86%E3%82%A3%E3%82%AC%E3%83%AC%E3%83%83%E3%82%AF%E3%82%B9%E4%BA%9C%E7%A8%AE%E3%81%AE%E3%83%87%E3%83%BC%E3%82%BF%E3%80%80%E7%B4%A0%E6%9D%90%E3%83%BB%E5%BC%B1%E7%82%B9%E8%82%89%E8%B3%AA%E3%83%BB%E9%83%A8%E4%BD%8D%E7%A0%B4%E5%A3%8A%E3%83%BB%E7%8A%B6%E6%85%8B%E8%80%90%E6%80%A7"
        resp (u/get-resource url)]
    (when (= 200 (-> resp :status))
      (let [body (:body resp)
            doc (Jsoup/parse body url)
            fp (-> (.body doc)
                 (.select ".page-content table")
                 first)
            max-length (reduce
                        (fn [val item]
                          (max val
                               (u/str-length (clojure.string/replace (.text item) #" " ""))))
                        0 (.select fp "td"))
            _ (println max-length)]
        (clojure.string/join
         "\n"
         (map-indexed
          (fn [index item]
            (if (= 0 index)
              (clojure.string/join
               " "
               (map (fn [s]
                      (format-field (.text s) max-length "，"))
                    (.select item "th")))
              (clojure.string/join
               "|"
               (cons (-> item
                       (.select "th")
                       (.text)
                       (format-field max-length "，"))
                     (map (fn [s]
                            (format-field (.text s) max-length "."))
                          (.select item "td"))))))
          (.select fp "tr")))))))

(defn mh-wiki-handler
  [req]
  (let [ret (get-from-mh-wiki)]
    (success {:text ret})))

(defn baike-wiki-handler
  [req]
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        to-search (clojure.string/trim
                   (clojure.string/replace text trigger-word ""))]
    (success {:text (get-from-wiki to-search)})))

(defn real-wiki-handler
  [req]
  (println req)
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        to-search (clojure.string/trim
                   (clojure.string/replace text trigger-word ""))]
    (success {:text (get-from-en-wiki to-search)})))
