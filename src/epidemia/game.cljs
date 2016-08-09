(ns epidemia.game
  (:require [epidemia.player :as plr]
            [epidemia.board :as brd]
            )
  )

(defprotocol IGame
  "Main game handling class"
  (can-make-step? [this player crd])
  (make-step [this player crd])
  )

(deftype Game [board players]
  IGame
  (can-make-step? [this player crd] 
    (let [
          cell-status (brd/get-cell-status (.-board this) crd)
          player-start-pos (plr/get-start-position (nth players player)) 
          ]
      (if (not= cell-status :empty-cell)
        false
        (or (= player-start-pos crd) (brd/cross-in-neighbors (.-board this) crd))
        )
      ))
  (make-step [this player crd]
    (brd/change-cell-status (.-board this) crd :crossed)
    )
  )

(defn init-game
  [board-size num-of-players]
  (let [board (brd/create-game-board board-size)
        corners (brd/get-corners board)
        numb (min num-of-players 4) ; there should be exception risen in case num-players > 4
        player-corners (take numb corners)
        players (map-indexed plr/create-player player-corners)
        ]
    (Game. board players)
    )
  )

