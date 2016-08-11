(ns epidemia.game
  (:require [epidemia.player :as plr]
            [epidemia.board :as brd]
            [epidemia.gamestate :as gmst]
            )
  )

(defprotocol IGame
  "Main game handling class"
  (available-for-step? [this crd])
 ; (is-enabled? [this crd])
  (can-make-step? [this crd])
  (make-step [this crd])
  (next-player [this])
  (get-step-details [this crd])
  (make-step-return-message [this crd])
  )

(deftype Game [board players game-settings game-state]
  IGame
  (available-for-step? [this crd]
     (let [
          player (gmst/get-active-player (.-game-state this))
          board (.-board this)
          cell-status (brd/get-cell-status board crd)
          cell-owner (brd/get-cell-owner board crd) 
          ]
      (if (= cell-status :empty-cell)
        true
        (if (= cell-status :filled)
          false
          (if (= cell-owner player)
          false
          true)))))

  (can-make-step? [this crd] 
    (let [
          player (gmst/get-active-player (.-game-state this))
          board (.-board this)
          player-start-pos (plr/get-start-position (nth players player)) 
          board (.-board this)
          ]
      (and (available-for-step? this crd)
           (or (= player-start-pos crd) (brd/is-enabled? board crd player )))))

  (make-step [this crd]
    (let [
          gm-state (.-game-state this)
          player (gmst/get-active-player gm-state)
          board (.-board this)
          ]
      (brd/player-moves-into board crd player)
      (gmst/dec-steps gm-state)
      (if (= 0 (gmst/get-steps-left gm-state))
        (next-player this)
        false
        )     
      ))

  (next-player [this]
    (let [
          gm-state (.-game-state this)
          player (gmst/get-active-player gm-state)
          num-of-players (:number-of-players (.-game-settings this))
          steps-per-move (:steps-per-move (.-game-settings this))
          ]
     (if (not= player (- num-of-players 1)) 
         (gmst/set-active-player gm-state (inc player))
         (gmst/set-active-player gm-state 0))
     (gmst/set-steps-left gm-state steps-per-move)))

  (get-step-details [this crd]
    (let [board (.-board this)
          cell-status (brd/get-cell-status board crd)
          gm-state (.-game-state this)
          active-player (gmst/get-active-player gm-state)
          new-cell-status (if (= cell-status :empty-cell) :crossed :filled)
          ]
      {:player active-player :status new-cell-status}
      )
    )

  (make-step-return-message [this crd]
    (let [
          step-details (get-step-details this crd)
          active-player (:player step-details)
          new-cell-status (:status step-details)
          change-turn? (make-step this crd)
          action (cond (= new-cell-status :crossed) " puts cross into "
                       (= new-cell-status :filled) " fills the cell ")
          str-crd (+ "(" (str (.-x crd) " " (str (.-y crd) ")")))
          message (+ "Player" (str active-player) action str-crd "\n")
          ]
      (if change-turn?
        (+ message "Next players turn\n")
        message)))
  
  )

(defn init-game 
  [game-settings]
  (let [board-size (:board-size game-settings)
        num-of-players (:number-of-players game-settings)
        board (brd/create-game-board board-size)
        corners (brd/get-corners board)
        numb (min num-of-players 4) ; there should be exception risen in case num-players > 4
        player-corners (take numb corners)
        players (map-indexed plr/create-player player-corners)
        game-state (gmst/GameState. 0 (:steps-per-move game-settings)) 
        ]
    (Game. board players game-settings game-state)
    )
  )

