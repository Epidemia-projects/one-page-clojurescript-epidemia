(ns epidemia.cell
  (:require [epidemia.coord :as crd])
  )

;(def Coord crd/Coord)

(defprotocol ICell
  "Describes a cell on game board"
  (get-cell-status [this])
  (set-cell-status [this new-status])
  (get-coord [this])
  )

(deftype Cell [crd status]
  ICell
  (get-cell-status [this] status)
  (set-cell-status [this new-status] (set! (.-status this) new-status))
  (get-coord [this] crd)
  )

(defn make-new-cell [x y]
 (Cell. (crd/Coord. x y) :empty-cell))
