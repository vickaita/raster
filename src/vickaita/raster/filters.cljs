(ns vickaita.raster.filters
  (:require-macros [vickaita.raster.macros :refer [dopixels]])
  (:require [vickaita.raster.core :as c :refer [image-data width height data]]
            [vickaita.raster.geometry :refer [surround]]))

(defn invert
  [img]
  (dopixels [[r g b a] img]
            [(- 255 r) (- 255 g) (- 255 b) a]))

(defn desaturate
  [img]
  (dopixels [[r g b a] img
             :let [avg (/ (+ r g b) 3)]]
            [avg avg avg a]))

(defn blur
  [img]
  (c/convolute [0 1 0
                1 1 1
                0 1 0] 5 0 img))
