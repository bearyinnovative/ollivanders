(ns simple-web-service.core
  (:require [ring.adapter.jetty :refer :all])
  (:require [simple-web-service.route :refer [app]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Start Simple Web Service")
  (run-jetty app {:port 8888
                  :max-threads 20}))
