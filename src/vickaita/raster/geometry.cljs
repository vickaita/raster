(ns vickaita.raster.geometry)

(defn surround
  "Generates a list of points within radius around the origin point."
  [radius [origin-x origin-y]]
  (for [y (range (- origin-y radius) (+ origin-y (inc radius)))
        x (range (- origin-x radius) (+ origin-x (inc radius)))]
    [x y]))

(defn pixel-groups
  "Generate a list of lists of points surrounding each point between (0, 0) and
  (w, h)."
  [w h r]
  (map (partial surround r)
       (for [y (range h) x (range w)] [x y])))

(defn normalize-matrix
  [m]
  ;; Handle the center cell of the matrix separately.
  (let [ci (inc (Math/floor (/ (count m) 2)))
        cv (nth m ci)
        cm (if (number? cv) (assoc m ci [cv cv cv 1]) m)]
    (map (fn [i] (if (number? i) [i i i i] i)) cm)))

