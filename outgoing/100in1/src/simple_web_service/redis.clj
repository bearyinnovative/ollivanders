(ns simple-web-service.redis
  (:refer-clojure :exclude [get set keys])
  (:require [taoensso.carmine :as car]))

(defmacro wcar* [conn-params & body]
  `(car/wcar ~conn-params ~@body))

(defmacro def-redis-n
  [conn-params name]
  (let [method (resolve (symbol (str "taoensso.carmine/" name)))]
    (assert method (format "Redis method [%s] not found" name))
    `(defn ~name [& args#]
       (wcar* ~conn-params (apply ~method args#)))))

(defmacro def-multi-redis-n
  [conn-params & names]
  (let [map-fn (fn [name]
                 `(def-redis-n ~conn-params ~name))]
    `(do
       ~@(map map-fn names))))

(defonce redis-conn-params
  {:pool {}
   :spec {:host "localhost"
          :port 6379}})

(def-multi-redis-n redis-conn-params
  get set setex del exists incr expire keys hset hget hdel
  hexists hgetall zadd zrangebyscore zremrangebyscore sadd
  srem smembers sismember scard publish subscribe flushall
  lpush llen rpop lrange ltrim hmset hlen hvals)
