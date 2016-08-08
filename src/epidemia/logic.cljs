(ns epidemia.logic
  ;;(:require epidemia.core)
  )

(def board-size 15)

(def player1-start-position [0 0])

(def Game (volatile! {}))

(defn create-game-board 
  [board_size]
  (vec 
   (for [x (range board_size)]
    (vec
     (for [y (range board_size)]
      (volatile! {:x x
                  :y y
                  :status :empty-cell
                  })
     )    
    )
   )
  )
) 

(defn return-neighbor-coords
  [[x y]]
   (for [x1 [-1 0 1]
         y1 [-1 0 1]
         :when (and (not= x1 y1 0)
                (<= 0 (+ x1 x))
                (<= 0 (+ y1 y))
                (> board-size (+ x1 x))
                (> board-size (+ y1 y))
                )
         ]
    [(+ x x1) (+ y y1)]     
    )
  ) 

(defn return-cell-neighbor-coords
  [cell]
  (let [
        x (:x cell)
        y (:y cell)
        ]
    (return-neighbor-coords [x y]) 
   )
  )

(defn get-cell
  [
   [x y]
   ]
  (deref (((:game-board (deref Game)) x) y) )
  )

(defn return-neighbor-cells
  [cell]
  (map get-cell (return-cell-neighbor-coords cell))
  )

(defn my-or [a b] (or a b))

(defn cross-in-neighbor?
  [cell]
  (let [neighbors (return-neighbor-cells cell)]
    (reduce my-or false (
                map (fn [_cell]
                      (= (:status _cell) :crossed)
                      )
                neighbors )) 
    )
  )

(defn possible-to-make-step?
  [cell]
  (let [
        x (:x cell)
        y (:y cell)
        ]
    (if (= (:status cell) :empty-cell)
      (if (= [x y] player1-start-position)
        true
        (cross-in-neighbor? cell)
        )
      false
      )
    )
  )

(defn cross-the-cell
  [cell]
  (let [
        x (:x cell)
        y (:y cell)
        new_cell cell
        ]
    (vreset! new_cell {:x x :y y :status :crossed})
    )
  )

(defn make-step
  [
   [x y]
   ]
  (let [
        cell (get-cell [x y])
        ]
    (if (possible-to-make-step? cell)
      (
        (cross-the-cell cell)
        true
        )
      false
    ) 
    )
  )

(defn init-game
  []
  (vreset! Game {:game-board (create-game-board board-size)})
  )
