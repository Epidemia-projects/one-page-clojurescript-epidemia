(ns epidemia.player
  (:require [epidemia.coord :as crd])
  )

(defprotocol IPlayer
  "Player class"
  (get-start-position [this] "Return starting position for a player")
  (is-alive? [this] "Does this player still can make a move?")
  )

; Implementation of IPlayer
(deftype Player [number start-pos alive?]
  IPlayer
  (get-start-position [this] start-pos)
  (is-alive? [this] alive?)
  )

(defn create-player
  "Create new player"
  [number start-position]
  (Player. number start-position true)
  )
