(ns hcrawler-runner.main
  (:require [environ.core :refer [env]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [hcrawler-runner.core :refer [extract-file]]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]))

(def rabbit-host (or (env :rabbit-host) "localhost"))

(defn copy [uri file]
  (if (not (.exists (io/file file)))
    (with-open [in (io/input-stream uri)
                out (io/output-stream file)]
      (io/copy in out))))

(defn create-folder [username]
  (.mkdir (java.io.File. (str "out/" username))))

(defn download-image [post]
  (create-folder (:username post))
  (copy (:url post)
        (str "out/" (:username post) "/" (:id post) ".jpeg")))

(defn download-video [post]
  (create-folder (:username post))
  (copy (:url post)
        (str "out/" (:username post) "/" (:id post) ".mp4")))

(defn download [post]
  (case (:type post)
    :image (download-image post)
    :video (download-video post)
    :carousel (doseq [media (:medias post)]
                (download (assoc media :username (:username post))))))

(defn in [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [json-str (String. payload "UTF-8")
        queue-item (json/read-str json-str :key-fn keyword)
        file-desc (extract-file queue-item)]
    (download file-desc)))

(defn wrap_int [in]
  (let [conn (rmq/connect {:host rabbit-host})
        ch (lch/open conn)
        qname "instagram"]
    (lq/declare ch qname {:exclusive false :auto-delete false})
    (lc/subscribe ch qname in {:auto-ack true})))

(gen-class
  :name hcrawler_runner.main
  :methods [[main [] String]])

(defn -main [& args]
  (wrap_int in))

