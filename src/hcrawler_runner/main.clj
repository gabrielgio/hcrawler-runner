(ns hcrawler-runner.main
  (:require [environ.core :refer [env]]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]))

(def rabbit-host (or (env :rabbit-host) "localhost"))

(defn in [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn wrap_int [in]
  (let [conn (rmq/connect {:host rabbit-host})
        ch (lch/open conn)
        qname "instagram"]
    (lq/declare ch qname {:exclusive false :auto-delete false})
    (lc/subscribe ch qname in {:auto-ack true})))


(defn -main [& args])
