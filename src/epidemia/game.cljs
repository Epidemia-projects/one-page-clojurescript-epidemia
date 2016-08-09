(ns epidemia.game
  (:require [epidemia.player :as plr]
            [epidemia.board :as brd]
            )
  )

(deftype Game [board players])

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

