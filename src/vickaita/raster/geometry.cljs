(ns vickaita.raster.geometry)

(defn surround
  "Generates a list of points within radius around the origin point."
  [radius [origin-x origin-y]]
  (for [x (range (- origin-x radius) (+ origin-x (inc radius)))
        y (range (- origin-y radius) (+ origin-y (inc radius)))]
    [x y]))

(defn pixel-groups
  "Generate a list of lists of points surrounding each point between (0, 0) and
  (w, h)."
  [w h r]
  (map (partial surround r)
       (for [x (range w) y (range h)] [x y])))

