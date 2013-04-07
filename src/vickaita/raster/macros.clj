(ns vickaita.raster.macros)

(defmacro defpixelmap
  [fname form & body]
  `(defn ~fname
     [src-img#]
     (let [w# (vickaita.raster.core/width src-img#)
           h# (vickaita.raster.core/height src-img#)
           c# (count src-img#)  
           src-data# (vickaita.raster.core/data src-img#)
           dst-img# (vickaita.raster.core/image-data w# h#)
           dst-data# (vickaita.raster.core/data dst-img#)]
       (dotimes [i# c#]
         (let [red-offset# (* 4 i#)
               green-offset# (+ 1 red-offset#)
               blue-offset# (+ 2 red-offset#)
               alpha-offset# (+ 3 red-offset#)
               ~(nth form 0) (aget src-data# red-offset#)
               ~(nth form 1) (aget src-data# green-offset#)
               ~(nth form 2) (aget src-data# blue-offset#)
               ~(nth form 3) (aget src-data# alpha-offset#)]
           (aset dst-data# red-offset# ~(nth (last body) 0))
           (aset dst-data# green-offset# ~(nth (last body) 1))
           (aset dst-data# blue-offset# ~(nth (last body) 2))
           (aset dst-data# alpha-offset# ~(nth (last body) 3))))
       dst-img#)))
