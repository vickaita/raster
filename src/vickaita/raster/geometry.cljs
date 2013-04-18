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
  (map #(if (number? %) [% % % %] %) m))

(defn convolution-table
  "Assumes that matrix is square."
  [m]
  (let [radius (bit-shift-right (Math/sqrt (count m)) 1)
        s (* -1 radius)
        e (inc radius)
        coords (for [y (range s e) x (range s e)] [x y])
        matrix (map #(if (number? %) [% % % %] %) m)]
    (into-array (flatten (remove #(= [0 0 0 0] (second %))
                            (map #(vector %1 %2) coords matrix))))))

(convolution-table [0 1 0
                    1 1 1
                    0 1 0])
