(ns vickaita.raster.core
  (:require [goog.dom :as dom]))

(declare image-data)

(def empty-pixel [0 0 0 0])

(defn pixel-array
  "Convert data into a Uint8ClampedArray."
  [data]
  (cond
    (= js/Uint8ClampedArray (type data)) data
    (coll? data) (if (map? (first data))
                  (js/Uint8ClampedArray. (into-array (flatten (map vals data))))
                  (js/Uint8ClampedArray. (into-array (flatten data))))))

(defn make-canvas
  "Create a canvas HTML tag. If width and height are provided then they will be
  set."
  ([] (dom/createElement "canvas"))
  ([w h]
   (let [cnv (dom/createElement "canvas")]
     (set! (.-width cnv) w)
     (set! (.-height cnv) h)
     cnv)))

(defn get-context
  "Gets a js/CanvasRenderingContext2D, either from a provided canvas or from a
  new canvas."
  ([] (get-context (make-canvas)))
  ([canvas] (.getContext canvas "2d"))
  ([w h] (get-context (make-canvas w h))))

(defn put-image
  "Draws an image-data onto a canvas."
  [cnv img]
  (let [i (image-data img)]
    (.putImageData (get-context cnv) i 0 0)))

;; IImageData Protocol
;; Provides a protocol for accessing ImageData of various HTML Elements as well
;; as some objects.

(defprotocol IImageData
  (-image-data [this] "Returns a js/ImageData object."))

(defn image-data
  "Returns a js/ImageData object from the provided object. If width and height
  are provided then a blank ImageData will be returned with the corresponding
  dimensions."
  ([i] (-image-data i))
  ([w h] (-image-data (make-canvas w h)))
  ([w h d] (-image-data {:width w :height h :data d})))

(extend-protocol IImageData
  js/ImageData
  (-image-data [this] this)

  js/Image
  (-image-data [img]
    (let [w (.-width img)
          h (.-height img)
          ctx (get-context w h)]
      (.drawImage ctx img 0 0 w h)
      (.getImageData ctx 0 0 w h)))

  js/HTMLImageElement
  (-image-data [img]
    (let [w (.-width img)
          h (.-height img)
          ctx (get-context w h)]
      (.drawImage ctx img 0 0 w h)
      (.getImageData ctx 0 0 w h)))

  js/HTMLCanvasElement
  (-image-data [canvas]
    (image-data (.getContext canvas "2d")))

  js/CanvasRenderingContext2D
  (-image-data [ctx]
    (let [canvas (.-canvas ctx)
          w (.-width canvas)
          h (.-height canvas)]
      (.getImageData ctx 0 0 w h)))

  cljs.core/ObjMap
  (-image-data [{:keys [width height data]}]
    (when (and width height data)
      (let [blank (image-data width height)]
        (.set (.-data blank) (pixel-array data)
              #_(if (= js/Uint8ClampedArray (type data))
                               data
                               (js/Uint8ClampedArray.
                                 (into-array (if) data))))
        blank)))

  )

;; IPixel Protocol
;; A protocol for accessing the color channels from a pixel.

(defprotocol IPixel
  (red [pixel] "The red component of the pixel.")
  (green [pixel] "The green component of the pixel.")
  (blue [pixel] "The blue component of the pixel.")
  (alpha [pixel] "The alpha component of the pixel."))

(extend-protocol IPixel
  js/Array
  js/Uint8ClampedArray
  (red [a] (aget a 0))
  (green [a] (aget a 1))
  (blue [a] (aget a 2))
  (alpha [a] (aget a 3)))

;; IBitmap Protocol
;; Provides a protocol for accessing dimensions and pixel data of an image.

(defprotocol IBitmap
  (width [bitmap] "The width of the bitmap in pixels.")
  (height [bitmap] "The height of the bitmap in pixels.")
  (data [bitmap] "The pixel data of the image." ))

;; ImageDataSeq
;; Allows seq functions to be called on an ImageData.

(deftype ImageDataSeq [w h arr i]

  ;Object
  ;(toString [this]
  ;  (pr-str this))

  ;IPrintWithWriter
  ;(-pr-writer [coll writer opts]
  ;  (pr-sequential-writer writer pr-writer "(" " " ")" opts coll))

  IPixel
  (red [_] (aget arr (* 4 i)))
  (green [_] (aget arr (+ 1 (* 4 i))))
  (blue [_] (aget arr (+ 2 (* 4 i))))
  (alpha [_] (aget arr (+ 3 (* 4 i))))

  IBitmap
  (width [_] w)
  (height [_] h)
  (data [_] arr)
  
  ISeqable
  (-seq [this] this)

  ASeq
  ISeq
  (-first [_] (let [offset (* 4 i)]
                [(aget arr offset)
                 (aget arr (+ 1 offset))
                 (aget arr (+ 2 offset))
                 (aget arr (+ 3 offset))]))
  (-rest [_] (if (< (* (inc i) 4) (alength arr))
                 (ImageDataSeq. w h arr (inc i))
                 (list)))

  INext
  (-next [_] (if (< (* (inc i) 4) (alength arr))
                 (ImageDataSeq. w h arr (inc i))
                 nil))

  ICounted
  (-count [_] (- (/ (alength arr) 4) i)) 

  IIndexed
  (-nth [coll n]
    (-nth coll n empty-pixel))
  (-nth [coll n not-found]
    (let [off (* 4 (+ n i))]
      (if (< i (alength arr))
        [(aget arr i)
         (aget arr (+ 1 i))
         (aget arr (+ 2 i))
         (aget arr (+ 3 i))]
        not-found)))

  ISequential
  IEquiv
  (-equiv [coll other] (equiv-sequential coll other))

  IEmptyableCollection
  (-empty [coll] cljs.core.List/EMPTY)

  IReduce
  (-reduce [coll f]
    (if (counted? arr)
      (ci-reduce arr f (aget arr i) (inc i))
      (ci-reduce coll f (aget arr i) 0)))
  (-reduce [coll f start]
    (if (counted? arr)
      (ci-reduce arr f start i)
      (ci-reduce coll f start 0)))

  IHash
  (-hash [coll] (hash-coll coll))

  IReversible
  (-rseq [coll]
    (let [c (-count coll)]
      (if (pos? c)
        (RSeq. coll (dec c) nil)
        ()))))

;; ImageData
;; Extend the native ImageData type with ClojureScript protocols so that it can
;; be operated on with standard collection functions.

(extend-type js/ImageData

  IBitmap
  (width [img] (.-width img))
  (height [img] (.-height img))
  (data [img] (.-data img))

  ICounted
  (-count [coll] (* (.-width coll) (.-height coll)))

  IIndexed
  (-nth [coll n]
    (-nth coll n empty-pixel))
  (-nth [coll n not-found]
    (let [pix (.-data coll)
          offset (* 4 n)]
      (if (and (>= n 0) (< n (count coll)))
        [(aget pix offset)        ; red
         (aget pix (+ 1 offset))  ; green
         (aget pix (+ 2 offset))  ; blue
         (aget pix (+ 3 offset))] ; alpha
        not-found)))

  ILookup
  (-lookup [coll k]
    (-lookup coll k empty-pixel))
  (-lookup [coll [x y] not-found]
    (-nth coll (+ x (* y (.-width coll))) not-found))

  IAssociative
  (-contains-key? [coll [x y]]
    (and (>= x 0)
         (>= y 0)
         (< x (.-width coll))
         (< y (.-height coll))))

  IFn
  (-invoke
    ([coll k]
     (-lookup coll k))
    ([coll k not-found]
     (-lookup coll k not-found)))

  ISeqable
  (-seq [coll]
    (ImageDataSeq. (.-width coll) (.-height coll) (.-data coll) 0))

)
