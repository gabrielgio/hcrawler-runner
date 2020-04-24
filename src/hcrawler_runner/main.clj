(ns hcrawler-runner.main
  (:gen-class)
  (:require [environ.core :refer [env]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [hcrawler-runner.core :refer [extract-file]]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [camel-snake-kebab.core :as csk]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]))

(def rabbit-host (or (env :rabbit-host) "localhost"))
(def http-enable (or (env :http-enable) "false"))
(def service-host (or (env :service-host) "http://localhost/image"))

(defn copy [uri file]
  (if (not (.exists (io/file file)))
    (with-open [in (io/input-stream uri)
                out (io/output-stream file)]
      (io/copy in out))))

(defn create-folder [username]
  (.mkdir (java.io.File. (str "out/" username))))

(defn download [post]
  (create-folder (:username post))
  (copy (:url post)
        (:image-path post)))

(defn create-payload [post]
  {:body         (generate-string (dissoc post [:medias]) {:key-fn (fn [x] (csk/->camelCase (name x)))})
   :content-type :json})

(defn request [post]
  (try
    (if (= http-enable "true")
      (client/post service-host (create-payload post)))
    (catch Exception e
      (println e))))

(defn in [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [json-str (String. payload "UTF-8")
        queue-item (json/read-str json-str :key-fn keyword)
        file-descs (extract-file queue-item)]
    (doseq [file-desc file-descs]
      (download file-desc)
      (request file-desc))))

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

