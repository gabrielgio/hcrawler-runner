(ns hcrawler-runner.core
  (:require [environ.core :refer [env]])
  (:import (java.util Date TimeZone)
           (java.text SimpleDateFormat)))

(def instagram-url "https://www.instagram.com")

(defn convert-to-datetime [epoch]
  (let [format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss")
        datetime (Date. (* epoch 1000))
        tz (TimeZone/getTimeZone "UTC")]
    (.setTimeZone format tz)
    (.format format datetime)))

(defn merge-metadata [post data]
  (merge data {:post-url     (str instagram-url "/p/" (:code post))
               :profile-name (get-in post [:user :username])
               :profile-url  (str instagram-url "/" (get-in post [:user :username]))
               :source-name  "instagram"
               :username     (get-in post [:user :username])
               :created-on   (convert-to-datetime (:taken_at post))
               :source-url   instagram-url}))

(defn extract-video [post]
  {:url        (get-in post [:video_versions 0 :url])
   :id         (get-in post [:id])
   :image-path (str "out/" (get-in post [:user :username]) "/" (:id post) ".mp4")
   :type       :video})

(defn extract-image [post]
  {:url        (get-in post [:image_versions2 :candidates 0 :url])
   :id         (get-in post [:id])
   :type       :image
   :image-path (str "out/" (get-in post [:user :username]) "/" (:id post) ".jpeg")})

(defn extract-single [item]
  (let [media-type (:media_type item)]
    (case media-type
      1 (extract-image item)
      2 (extract-video item)
      (println media-type))))

(defn extract-carousel [post]
  (->> (:carousel_media post)
       (map #(merge % {:user (:user post)}))
       (map extract-single)))


(defn extract-file [queue-item]
  (let [post (:post queue-item)
        media-type (:media_type post)]
    (if (some (partial = media-type) [1 2])
      [(merge-metadata post (extract-single post))]
      (map #(merge-metadata post %) (extract-carousel post)))))