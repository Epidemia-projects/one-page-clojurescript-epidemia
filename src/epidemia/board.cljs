(ns epidemia.board
  (:require [epidemia.coord :as crd]
            [epidemia.cell :as cll]
            ;[clojure.set]
            )
  )

;(def Coord epidemia.coord/Coord)
;(def Cell epidemia.cell/Cell)
;(def make-new-cell epidemia.cell/make-new-cell)

(defn my-or [a b] (or a b))

(defn reduce-or [_seq]
  (reduce my-or false _seq))

(defn reduce-or-on-map [_fn _seq]
  (reduce-or (map _fn _seq)))

(defn my-conj [_set v]
  (if (reduce-or-on-map (fn [el] (= el v)) _set)
    _set
    (conj _set v)))

(defn my-disj [_set v]
  (if (reduce-or-on-map (fn [el] (= el v)) _set)
    (disj _set v)
    _set))

(defn union [s1 s2]
  (if (< (count s1) (count s2))
    (reduce my-conj s2 s1)
    (reduce my-conj s1 s2)))

(defn difference [s1 s2]
  (reduce my-disj s1 s2))

(defprotocol IBoard
  "This object should contain state of game board and relevant methods"
  (get-cell [this v])
  (get-cell-status [this v])
  (get-cell-owner [this v])
  (change-cell-status [this v new-status])
  (get-cell-neighbors-coords [this v])
  (get-cell-neighbors [this v])
  (is-empty? [this v])
  (get-corners [this])
  (has-friendly-cross? [this v-set player])
  (player-moves-into [this v player])
  (is-enabled? [this v player])
  (is-enabled-recur [this player checked to-check])
  )

(deftype Board [board board-size]
  IBoard
  (get-cell [this v]
    (cond (= (type v) crd/Coord) 
                                (let [x (.-x v)
                                      y (.-y v)]
                                      (((.-board this) x) y)
                                  ) 
          (= (type v) cll/Cell) v))

  (get-cell-status [this v]
    (cll/get-cell-status (get-cell this v)))

  (get-cell-owner [this v]
    (cll/get-owner (get-cell this v)))

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
  (get-corners [this]
    (let [bs  (- (.-board-size this) 1) ]
      (list (crd/Coord. 0 0)
            (crd/Coord. bs bs)
            (crd/Coord. 0 bs)
            (crd/Coord. bs 0)
            )
      )
    )
  (has-friendly-cross? [this v-set player]
      (reduce-or-on-map
         (fn [v]
           (let [cell (get-cell this v)]
                (and (= (cll/get-cell-status cell) :crossed)
                (= (cll/get-owner cell) player))))
          v-set))

  (player-moves-into [this v player]
    (let [cell (get-cell this v)]
      (cll/player-moves-into cell player)))
 
  (is-enabled-recur [this player checked-cells to-check]
    (if (has-friendly-cross? this to-check player)
      true
      (let [
          filled-crds (filter (fn [v] 
                                (let [
                                      cell-status (get-cell-status this v)
                                      cell-owner (get-cell-owner this v)
                                      ]
                                  (and (= cell-status :filled)
                                       (= cell-owner player))))
                              to-check)
          ]
        (if (empty? filled-crds)
          false
          (let [
                new-checked (union checked-cells to-check)
                new-to-check  (difference 
                              (reduce union #{} 
                                  (map (fn [v] 
                                         (set (get-cell-neighbors-coords this v)))
                                       filled-crds))
                                new-checked)
              ]
            (recur this player new-checked new-to-check))))))

  (is-enabled? [this v player ]
    (let [
          neighbors (get-cell-neighbors-coords this v )
          ]
      (is-enabled-recur this player #{v} (set neighbors))))

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

