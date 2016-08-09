(ns epidemia.board
  (:require [epidemia.coord :as crd]
            [epidemia.cell :as cll])
  )

;(def Coord epidemia.coord/Coord)
;(def Cell epidemia.cell/Cell)
;(def make-new-cell epidemia.cell/make-new-cell)

(defprotocol IBoard
  "This object should contain state of game board and relevant methods"
  (get-cell [this v])
  (get-cell-status [this v])
  (change-cell-status [this v new-status])
  (get-cell-neighbors-coords [this v])
  (get-cell-neighbors [this v])
  (is-empty? [this v])
  )

(deftype Board [board board-size]
  IBoard
  (get-cell [this v]
    (cond (= (type v) crd/Coord) 
                                (let [x (.-x v)
                                      y (.-y v)]
                                      (((.-board this) x) y)
                                  ) 
          (= (type v) cll/Cell)
                                (let [cord (cll/get-coord v)
                                      x (.-x cord)
                                      y (.-y cord)
                                      ]
                                  (((.-board this) x) y)
                                  )))
  (get-cell-status [this v]
    (cll/get-cell-status (get-cell this v)))

  (change-cell-status [this v new-status]
    (cll/set-cell-status (get-cell this v) new-status))

  (get-cell-neighbors-coords [this v]
    (cond (= (type v) crd/Coord) 
                                (crd/get-neighbor-coords v (.-board-size this))
          (= (type v) cll/Cell)
                                (let [ cord (cll/get-coord v) ]
                                      (crd/get-neighbor-coords cord (.-board-size this)) 
                                  )
          )
    )

  (get-cell-neighbors [this v]
    (map (fn [cord] (get-cell this cord)) (get-cell-neighbors-coords this v))
    )
  (is-empty? [this v]
    (if (= (get-cell-status this v) :empty-cell) true false)
    )
  )

(defn create-game-board 
  [board-size]
  (Board.
    (vec 
      (for [x (range board-size)]
        (vec
          (for [y (range board-size)]
            (cll/make-new-cell x y)))))
    board-size))

