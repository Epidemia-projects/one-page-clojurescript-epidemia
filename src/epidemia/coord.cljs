(ns epidemia.coord)

(defprotocol ICoordinates
  "Add a layer of abstraction from 2d"
  (get-neighbor-coords [this brd-size] "Get neighboring coordintes to a given coordinate")
  )

; A 2D coordinate
(deftype Coord [x y])


(defn make-coord
  "Create new 2d coordinate object"
  [x y]
  (Coord. x y)
  )

(extend-type Coord
  ICoordinates
  (get-neighbor-coords [this brd-size]
    (let [x (.-x this)
          y (.-y this)
          ]
     (for [X [-1 0 1]
           Y [-1 0 1]
           :when (and (not= X Y 0)
                      (<= 0 (+ x X))
                      (<= 0 (+ y Y))
                      (> brd-size (+ x X))
                      (> brd-size (+ y Y))
                      )
           ]
       (Coord. (+ x X) (+ y Y))
       ))
    )
  IEquiv
  (-equiv [this other]
    (and 
      (= (type this) (type other))
      (= (.-x this) (.-x other))
      (= (.-y this) (.-y other))
      )
    )
  )
