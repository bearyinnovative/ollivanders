(ns simple-web-service.route
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [simple-web-service.utils :refer [wrap-json-params]]
            [simple-web-service.handlers.wiki :as wiki]
            [simple-web-service.handlers.roll :as roll]
            [simple-web-service.handlers.giphy :as giphy]))

(defroutes app-routes
  (POST "/wiki" [] wiki/baike-wiki-handler)
  (POST "/twiki" [] wiki/real-wiki-handler)
  (POST "/roll" [] roll/roll)
  (POST "/rollv" [] roll/roll-with-n-dice)
  (POST "/giphy" [] giphy/giphy)
  (POST "/weakness" [] wiki/mh-wiki-handler))

(def app (handler/site (-> app-routes
                         wrap-json-params)))
