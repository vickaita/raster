(ns vickaita.raster.filters
  (:require-macros [vickaita.raster.macros :refer [dopixel]])
  (:require [vickaita.raster.core :as c :refer [image-data width height data]]
            [vickaita.raster.geometry :refer [surround]]))

(defn invert
  [img]
  (dopixel [[r g b a] img]
           [(- 255 r) (- 255 g) (- 255 b) a]))
