(ns core-test
  (:require [hcrawler-runner.core :refer :all]
            [clojure.test :refer :all]
            [clojure.data.json :as json]))

(def single-video-url "https://scontent-dus1-1.cdninstagram.com/v/t50.2886-16/18320084_249828945490982_1543373000051523584_n.mp4?_nc_ht=scontent-dus1-1.cdninstagram.com&_nc_cat=102&_nc_ohc=H5Yjaw5urIEAX-gAN2H&oe=5E08CCA1&oh=65accf2cf047136a277bbcd851f99d74")
(def single-image-url "https://scontent-dus1-1.cdninstagram.com/v/t51.2885-15/e35/79437981_571401397020124_5748988122873669093_n.jpg?_nc_ht=scontent-dus1-1.cdninstagram.com&_nc_cat=104&_nc_ohc=w4looW95wtsAX_8LFOm&se=8&oh=4e0ee3c0f8ceaeadfcf8dc75d55b7e17&oe=5EA876FB&ig_cache_key=MjE5NTk2MDQ0OTI1NDMwNjA2Nw%3D%3D.2")
(def carousel-image-urls ["https://scontent-dus1-1.cdninstagram.com/v/t51.2885-15/e35/78947089_165887404487594_8524794999407706645_n.jpg?_nc_ht=scontent-dus1-1.cdninstagram.com&_nc_cat=102&_nc_ohc=aOZNpIMSD40AX8uW2XZ&oh=03fbeabb1987ddf34a43b5f618ffe501&oe=5EA6EEA4&ig_cache_key=MjE5NTYzNTQ1NjM2NjkyMjg4Ng%3D%3D.2"
                          "https://scontent-dus1-1.cdninstagram.com/v/t51.2885-15/e35/75358239_209813520028671_4418867741051929954_n.jpg?_nc_ht=scontent-dus1-1.cdninstagram.com&_nc_cat=103&_nc_ohc=tdWnAmz1pA4AX-uc1zE&oh=9c535f4f99ec141cd49fca003ef048b8&oe=5EA81A3D&ig_cache_key=MjE5NTYzNTQ1NjM1MDE1MDk2OA%3D%3D.2"])

(defn read-post! [filename extract-fn]
  (let [json-str (slurp filename)
        queue-item (json/read-str json-str :key-fn keyword)]
    (extract-fn (:post queue-item))))


(defn read! [filename extract-fn]
  (let [json-str (slurp filename)
        queue-item (json/read-str json-str :key-fn keyword)]
    (extract-fn queue-item)))

(deftest extract
  (testing "extract single video"
    (let [video (read-post! "test/data/video.json" extract-video)]
      (is (= single-video-url (:url video)))
      (is (= :video (:type video)))
      (is (= "out/baesuicide/1503214870974369621_703072962.mp4" (:image-path video)))
      (is (= "1503214870974369621_703072962" (:id video)))))
  (testing "extract single image"
    (let [image (read-post! "test/data/image.json" extract-image)]
      (is (= single-image-url (:url image)))
      (is (= :image (:type image)))
      (is (= "out/maple.pepe_/2195960449254306067_21933169435.jpeg" (:image-path image)))
      (is (= "2195960449254306067_21933169435" (:id image)))))
  (testing "extract carousel"
    (let [carousels (read-post! "test/data/carousel.json" extract-carousel)]
      (is (= 2 (count carousels)))
      (let [carousel (first carousels)
            url (first carousel-image-urls)]
        (is (= :image (:type carousel)))
        (is (= url (:url carousel)))
        (is (= "out/maple.pepe_/2195635456366922886_21933169435.jpeg" (:image-path carousel)))
        (is (= "2195635456366922886_21933169435" (:id carousel))))
      (let [carousel (last carousels)
            url (last carousel-image-urls)]
        (is (= :image (:type carousel)))
        (is (= url (:url carousel)))
        (is (= "out/maple.pepe_/2195635456350150968_21933169435.jpeg" (:image-path carousel)))
        (is (= "2195635456350150968_21933169435" (:id carousel))))))
  (testing "extract single file image"
    (let [images (read! "test/data/image.json" extract-file)]
      (is (= 1 (count images)))
      (let [image (first images)]
        (is (= :image (:type image)))
        (is (= "instagram" (:source-name image)))
        (is (= "https://www.instagram.com" (:source-url image)))
        (is (= "maple.pepe_" (:profile-name image)))
        (is (= "2019-12-10T17:28:26" (:created-on image)))
        (is (= "https://www.instagram.com/maple.pepe_" (:profile-url image)))
        (is (= "https://www.instagram.com/p/B55nmTWnDET" (:post-url image))))))
  (testing "extract single file carousel"
    (let [carousels (read! "test/data/carousel.json" extract-file)]
      (doseq [carousel carousels]
        (is (= :image (:type carousel)))
        (is (= "instagram" (:source-name carousel)))
        (is (= "https://www.instagram.com" (:source-url carousel)))
        (is (= "maple.pepe_" (:profile-name carousel)))
        (is (= "2019-12-10T06:42:44" (:created-on carousel)))
        (is (= "https://www.instagram.com/maple.pepe_" (:profile-url carousel)))
        (is (= "https://www.instagram.com/p/B54dtEfANk-" (:post-url carousel))))))
  (testing "extract single file video"
    (let [videos (read! "test/data/video.json" extract-file)]
      (is (= 1 (count videos)))
      (let [video (first videos)]
        (is (= "instagram" (:source-name video)))
        (is (= "https://www.instagram.com" (:source-url video)))
        (is (= "baesuicide" (:profile-name video)))
        (is (= "2017-04-28T22:06:38" (:created-on video)))
        (is (= "https://www.instagram.com/baesuicide" (:profile-url video)))
        (is (= "https://www.instagram.com/p/BTcffX1gw9V" (:post-url video)))
        (is (= :video (:type video)))))))

