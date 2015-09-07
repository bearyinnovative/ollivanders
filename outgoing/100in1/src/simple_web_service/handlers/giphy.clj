(ns simple-web-service.handlers.giphy
  (:require [simple-web-service.utils :as u :refer [success]])
  (:require [clojure.data.json :as json]
            [clojure.string :as cstr]))

(defn- get-from-giphy [keyword]
  (let [url (format "http://api.giphy.com/v1/gifs/search?api_key=dc6zaTOxFJmzC&limit=1&q=%s"
                    (u/encode-url keyword))
        resp (u/get-resource url)]
    (when (= 200 (:status resp))
      (-> resp :body
          json/read-str
          (get "data")
          first
          (get-in ["images" "fixed_width_downsampled"])))))

(defn giphy [req]
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        keyword (cstr/trim
                 (cstr/replace text trigger-word ""))]
    (when-let [img (get-from-giphy keyword)]
      (let [url (get img "url")
            width (get img "width")
            height (get img "height")]
        (success {:text keyword
                  :fallback keyword
                  :attachments [{"images" [{"url" url
                                             "height" height
                                             "width" width}]}]})))))
