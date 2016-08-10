(ns epidemia.cell
  (:require [epidemia.coord :as crd])
  )

;(def Coord crd/Coord)

(defprotocol ICell
  "Describes a cell on game board"
  (get-cell-status [this])
  (set-cell-status [this new-status])
  (get-coord [this])
  (get-owner [this])
  (set-owner [this player-num])
  (player-moves-into [this player-num])
  )

(deftype Cell [crd status owner]
  ICell
  (get-cell-status [this] status)
  (set-cell-status [this new-status] (set! (.-status this) new-status))
  (get-coord [this] crd)
  (get-owner [this] owner)
  (set-owner [this player-num] (set! (.-owner this) player-num))
  (player-moves-into [this player-num] 
    (set-owner this player-num)
    (if (= (get-cell-status this) :empty-cell)
        (set-cell-status this :crossed)
        (set-cell-status this :filled))
    )
  )

(defn make-new-cell [x y]
 (Cell. (crd/Coord. x y) :empty-cell nil))
