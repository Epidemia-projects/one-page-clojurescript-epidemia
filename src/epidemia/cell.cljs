(ns epidemia.cell
  (:require [epidemia.coord :as crd])
  )

(defprotocol ICell
  "Describes a cell on game board"
  (get-cell-status [this] "Get status of the cell")
  (set-cell-status [this new-status] "Mute the cell status")
  (get-coord [this] "Get coordinate of this cell")
  (get-owner [this] "Get owner of the cell")
  (set-owner [this player-num] "Mute cell owner")
  (player-moves-into [this player-num] "Mute cell status according to player that moves into")
  )

; Implementation of  ICell
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

(defn make-new-cell
  "Create new empty cell"
  [x y]
 (Cell. (crd/Coord. x y) :empty-cell nil))
