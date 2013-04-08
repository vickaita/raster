(ns vickaita.raster.macros)

(defn- vec4?
  "Predicate to check if expr is a vector of four elements."
  [expr]
  (and (vector? expr)
       (= 4 (count expr))))

(defmacro dopixel
  [[form src-img] body]
  `(let [w# (vickaita.raster.core/width ~src-img)
         h# (vickaita.raster.core/height ~src-img)
         src-data# (vickaita.raster.core/data ~src-img)
         dst-img# (vickaita.raster.core/image-data w# h#)
         dst-data# (vickaita.raster.core/data dst-img#)
         n# (dec (* 4 (count ~src-img)))]
     (loop [r# 0 g# 1 b# 2 a# 3]
       (let [~(nth form 0) (aget src-data# r#)
             ~(nth form 1) (aget src-data# g#)
             ~(nth form 2) (aget src-data# b#)
             ~(nth form 3) (aget src-data# a#)]
         (aset dst-data# r# ~(nth body 0))
         (aset dst-data# g# ~(nth body 1))
         (aset dst-data# b# ~(nth body 2))
         (aset dst-data# a# ~(nth body 3)))
       (when (< a# n#)
         (recur (+ 4 r#) (+ 4 g#) (+ 4 b#) (+ 4 a#))))
     dst-img#))

(comment 

  (defmacro dopixel
    [[form src-img] & body]
    (if (and (= 1 (count body)) (vec4? (first body)))
      `(let [w# (vickaita.raster.core/width ~src-img)
             h# (vickaita.raster.core/height ~src-img)
             src-data# (vickaita.raster.core/data ~src-img)
             dst-img# (vickaita.raster.core/image-data w# h#)
             dst-data# (vickaita.raster.core/data dst-img#)
             n# (dec (* 4 (count ~src-img)))]
         (loop [r# 0 g# 1 b# 2 a# 3]
           (let [~(nth form 0) (aget src-data# r#)
                 ~(nth form 1) (aget src-data# g#)
                 ~(nth form 2) (aget src-data# b#)
                 ~(nth form 3) (aget src-data# a#)]
             (aset dst-data# r# ~(nth (first body) 0))
             (aset dst-data# g# ~(nth (first body) 1))
             (aset dst-data# b# ~(nth (first body) 2))
             (aset dst-data# a# ~(nth (first body) 3)))
           (when (< a# n#)
             (recur (+ 4 r#) (+ 4 g#) (+ 4 b#) (+ 4 a#))))
         dst-img#)
      `(let [w# (vickaita.raster.core/width ~src-img)
             h# (vickaita.raster.core/height ~src-img)
             src-data# (vickaita.raster.core/data ~src-img)
             dst-img# (vickaita.raster.core/image-data w# h#)
             dst-data# (vickaita.raster.core/data dst-img#)
             n# (dec (* 4 (count ~src-img)))]
         (loop [r# 0 g# 1 b# 2 a# 3]
           (let [~(nth form 0) (aget src-data# r#)
                 ~(nth form 1) (aget src-data# g#)
                 ~(nth form 2) (aget src-data# b#)
                 ~(nth form 3) (aget src-data# a#)
                 [rv# gv# bv# av# (do ~@body)]]
             (aset dst-data# r# rv#)
             (aset dst-data# g# gv#)
             (aset dst-data# b# bv#)
             (aset dst-data# a# av#))
           (when (< a# n#)
             (recur (+ 4 r#) (+ 4 g#) (+ 4 b#) (+ 4 a#))))
         dst-img#)
      ))

)
