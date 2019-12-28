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

(deftest extract
  (testing "extract single video"
    (let [video (read-post! "test/data/video.json" extract-video)]
      (is (= single-video-url (:url video)))
      (is (= :video (:type video)))
      (is (= "1503214870974369621_703072962" (:id video)))
      (is (= "baesuicide" (:username video)))))
  (testing "extract single image"
    (let [image (read-post! "test/data/image.json" extract-image)]
      (is (= single-image-url (:url image)))
      (is (= :image (:type image)))
      (is (= "2195960449254306067_21933169435" (:id image)))
      (is (= "maple.pepe_" (:username image)))))
  (testing "extract single file"
    (let [image (read-post! "test/data/image.json" extract-single)
          video (read-post! "test/data/video.json" extract-single)]
      (is (= :image (:type image)))
      (is (= :video (:type video)))))
  (testing "extract carousel"
    (let [carousel (read-post! "test/data/carousel.json" extract-carousel)]
      (is (= :carousel (:type carousel)))
      (is (= "maple.pepe_" (:username carousel)))
      (is (= 2 (count (:medias carousel))))
      (let [carousel-media (get-in carousel [:medias 0])]
        (is (= (get carousel-image-urls 0) (:url carousel-media)))
        (is (= :image (:type carousel-media)))
        (is (= "2195635456366922886_21933169435" (:id carousel-media))))
      (let [carousel-media (get-in carousel [:medias 1])]
        (is (= (get carousel-image-urls 1) (:url carousel-media)))
        (is (= :image (:type carousel-media)))
        (is (= "2195635456350150968_21933169435" (:id carousel-media)))))))

