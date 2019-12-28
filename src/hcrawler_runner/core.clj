(ns hcrawler-runner.core
  (:require [environ.core :refer [env]]
            [clojure.data.json :as json]))

(defn extract-video [post]
  {:url      (get-in post [:video_versions 0 :url])
   :id       (get-in post [:id])
   :type     :video
   :username (get-in post [:user :username])})

(defn extract-image [post]
  {:url      (get-in post [:image_versions2 :candidates 0 :url])
   :id       (get-in post [:id])
   :type     :image
   :username (get-in post [:user :username])})

(defn extract-single [item]
  (let [media-type (:media_type item)]
    (case media-type
      1 (extract-image item)
      2 (extract-video item))))

(defn extract-carousel [post]
  {:type     :carousel
   :username (get-in post [:user :username])
   :medias   (into-array (map extract-single (:carousel_media post)))})

(defn extract-file [queue-item]
  (let [post (:post queue-item)
        media-type (:media_type post)]
    (if (contains? [1 2] media-type)
      (extract-file post)
      (extract-carousel post))))