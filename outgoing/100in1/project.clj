(defproject simple-web-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.2"]
                 [ring/ring-servlet "1.2.2"]
                 [com.taoensso/carmine "2.6.2"]
                 [clj-http "0.9.2"]
                 [org.jsoup/jsoup "1.8.1"]
                 [org.clojure/data.json "0.2.5"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.1"]]
  :main simple-web-service.core
  :plugins [[lein-libdir "0.1.1"]]
  :target-path "target/%s")
