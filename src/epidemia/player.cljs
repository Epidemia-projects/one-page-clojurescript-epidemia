(ns epidemia.player
  (:require [epidemia.coord :as crd])
  )

(defprotocol IPlayer
  "Player class"
  (get-start-position [this])
  (is-alive? [this])
  )

(deftype Player [number start-pos alive?]
  IPlayer
  (get-start-position [this] start-pos)
  (is-alive? [this] alive?)
  )

(defn create-player
  [number start-position]
  (Player. number start-position true)
  )
