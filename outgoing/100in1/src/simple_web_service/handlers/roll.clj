(ns simple-web-service.handlers.roll
  (:require [simple-web-service.utils :as u :refer [success]]))

(defn- parse-params
  [req]
  (let [params (:params req)
        text (:text params)
        trigger-word (:trigger_word params)
        roll-str (clojure.string/trim
                  (clojure.string/replace text trigger-word ""))
        ns (clojure.string/split roll-str #"d")
        ret (->> (mapv u/->int ns)
              (filter identity))]
    (when (and ret
               (= 2 (count ret)))
      ret)))

(defn- error-response
  []
  (success {:text "你是黑客，{n}d{m} 才是对的哦"}))

(defn- random
  [dnum dsize]
  (mapv (fn [i] (rand-int (+ 1 dsize))) (range dnum)))

(defn roll [req]
  (if-let [[dnum dsize] (parse-params req)]
    (let [rets (random dnum dsize)]
      (success {:text (apply + rets)}))
    (error-response)))

(defn roll-with-n-dice [req]
  (if-let [[dnum dsize] (parse-params req)]
    (let [rets (random dnum dsize)
          dices (clojure.string/join "," rets)
          sum (apply + rets)]
      (success {:text (format "骰子: %s\r\n总和: %s" dices sum)}))
    (error-response)))
