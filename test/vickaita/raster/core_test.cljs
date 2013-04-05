(ns vickaita.raster.core-test
  (:require [cemerick.cljs.test :refer (test-ns)]
            [vickaita.raster.core :as r]
            [goog.dom :as dom])
  (:require-macros [cemerick.cljs.test :refer (is deftest run-tests testing)]))  

(deftest make-canvas-test
  (let [c (r/make-canvas 10 20)]
    (testing "creates the correct html tag"
      (is (= "CANVAS" (.-tagName c))))
    (testing "canvas has the correct dimensions"
      (is (= 10 (.-width c)))
      (is (= 20 (.-height c))))
    ))

(deftest get-context-test
  (testing "no args"
    (let [cx (r/get-context)]
      (is (= js/CanvasRenderingContext2D (type cx)))))
  (testing "with canvas tag specified"
    (let [c (r/make-canvas 10 20)
          cx (r/get-context c)]
      (is (= js/CanvasRenderingContext2D (type cx)))
      (is (= cx (.getContext c "2d")))
      (is (= (.-canvas cx) c))))
  (testing "with width and height"
    (let [cx (r/get-context 5 10)]
      (is (= js/CanvasRenderingContext2D (type cx)))
      ))
  )

(deftest image-data-test
  (testing "constructor with dimensions"
    (let [i (r/image-data 10 20)]
      (is (= js/ImageData (type i)))
      (is (= 10 (r/width i)) "should have the specified width")
      (is (= 20 (r/height i)) "should have the specified height")
      (is (= 800 (alength (.-data i))) "data should have 4 ints per pixel")
      ))
  (testing "constructor with img element"
    (let [img (dom/getElement "sample-image")
          i (r/image-data img)]
      (is (= js/ImageData (type i)))
      (is (= 500 (.-width i)))
      (is (= 264 (.-height i)))
      ))
  (testing "constructor with a map (Uint8ClampedArray)"
    (let [m {:width 1 :height 2
             :data (js/Uint8ClampedArray. (js/Array. 1 2 3 4 5 6 7 8))}
          i (r/image-data m)]
      (is (= js/ImageData (type i)))
      (is (= 1 (.-width i)))
      (is (= 2 (.-height i)))
      (is (= 8 (alength (.-data i))))
      ))
  (testing "constructor with a map (Seq)"
    (let [m {:width 1 :height 2 :data (range 8)}
          i (r/image-data m)]
      (is (= js/ImageData (type i)))
      (is (= 1 (.-width i)))
      (is (= 2 (.-height i)))
      (is (= 8 (alength (.-data i))))
      ))
  (testing "constructor with a map (LazySeq)"
    (let [m {:width 1 :height 2 :data (map identity (range 8))}
          i (r/image-data m)]
      (is (= js/ImageData (type i)))
      (is (= 1 (.-width i)))
      (is (= 2 (.-height i)))
      (is (= 8 (alength (.-data i))))
      ))
  )

(deftest image-data-implements-protocols
  (let [i (r/image-data {:width 10 :height 20 :data (range 800)})]
    (testing "ICounted"
      (is (= 200 (-count i)) "count should be the number of pixels"))
    (testing "Bitmap"
      (is (= 10 (r/width i)))
      (is (= 20 (r/height i)))
      (let [p (r/data i)]
        (is (= js/Uint8ClampedArray (type p)))
        (is (= (* 10 20 4) (alength p)))))
    (testing "IIndexed"
      (is (= {:r 0 :g 1 :b 2 :a 3} (-nth i 0)))
      (is (= {:r 0 :g 0 :b 0 :a 0} (-nth i -1))))
      (is (= "oops" (-nth i -1 "oops")))
    (testing "ILookup"
      (is (= {:r 0 :g 1 :b 2 :a 3} (-lookup i [0 0])))
      (is (= {:r 55 :g 56 :b 57 :a 58} (-lookup i [5 5])))
      (is (= {:r 0 :g 0 :b 0 :a 0} (-lookup i [-1 0])))
      (is (= "oops" (-lookup i [0 -1] "oops")))
      )
    (testing "IAssociative"
      (is (-contains-key? i [0 0]))
      (is (-contains-key? i [9 19]))
      (is (not (-contains-key? i [10 20])))
      (is (not (-contains-key? i [-1 0])))
      )
    (testing "IFn"
      (is (= {:r 0 :g 1 :b 2 :a 3} (i [0 0])))
      (is (= "oops" (i [-1 0] "oops")))
      )
    (testing "ISeqable"
      (is (seq? (-seq i)))
      (is (= {:r 0 :g 1 :b 2 :a 3} (first i)))
      )
    ))

#_(deftest seq-functions-test
  (let [i (r/image-data {:width 2 :height 1 :data (range 8)})]
    (testing "map"
      (is (= '({:a 3, :b 2, :g 1, :r 0}
               {:a 7, :b 6, :g 5, :r 4})
             (map identity i))))))

(test-ns 'vickaita.raster.core-test)
