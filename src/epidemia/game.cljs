(ns epidemia.game
  (:require [epidemia.player :as plr]
            [epidemia.board :as brd]
            [epidemia.gamestate :as gmst]
            )
  )

(defprotocol IGame
  "Main game handling class"
  (available-for-step? [this crd] "Check if a cell is empty or contains rival cross")
 ; (is-enabled? [this crd])
  (can-make-step? [this crd] "Check if cell is available for step and is enabled by the players own crosses")
  (make-step [this crd] "Mute the board, cycle players if a player made his last step in a move")
  (cycle-alive-players [this] "Cycle player queue")
  (next-player [this] "Cycle alive players, change current player and reset number of steps left in move")
  (get-step-details [this crd] "Return number of player making step and new cell status")
  (make-step-return-message [this crd] "Mute game board, cycles player if necessary, return a message for a game log")
  )

; Utility functions to operate on player queue
(defn make-queue [n]
  "Create a queue of size n"
  (reduce (fn [a b]
            (conj a b))
          #queue [] (range n)))

(defn que-cycle [que]
  "Get first element of a queue and put it in the end"
  (let [frst (peek que)
        rst (pop que)
        ]
    (conj rst frst)))
; need a function to get element out of queue

; IGame implementation
(deftype Game [board players game-settings game-state alive-players]
  IGame
  (available-for-step? [this crd]
     (let [
          player (peek (.-alive-players this))
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
          player (peek (.-alive-players this))
          board (.-board this)
          player-start-pos (plr/get-start-position (nth players player)) 
          board (.-board this)
          ]
      (and (available-for-step? this crd)
           (or (= player-start-pos crd) (brd/is-enabled? board crd player )))))

  (make-step [this crd]
    (let [
          gm-state (.-game-state this)
          player (peek (.-alive-players this))
          board (.-board this)
          ]
      (brd/player-moves-into board crd player) ; mute
      (gmst/dec-steps gm-state) ; mute
      (if (= 0 (gmst/get-steps-left gm-state))
        (next-player this) ; mute
        false
        )     
      ))

  (cycle-alive-players [this]
    (let [alive-players (.-alive-players this)]
      (set! (.-alive-players this) (que-cycle alive-players)))) ; mute

  (next-player [this]
    (let [
          gm-state (.-game-state this)
          active-players (cycle-alive-players this) ; mute
          new-player (peek (.-alive-players this))
          ; num-of-players (:number-of-players (.-game-settings this))
          steps-per-move (:steps-per-move (.-game-settings this))
          ]
     (gmst/set-active-player gm-state new-player)
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
          change-turn? (make-step this crd) ; mute
          action (cond (= new-cell-status :crossed) " puts cross into "
                       (= new-cell-status :filled) " fills the cell ")
          str-crd (+ "(" (str (.-x crd) " " (str (.-y crd) ")")))
          message (+ "Player" (str active-player) action str-crd "\n")
          ]
      (if change-turn?
        (+ message "Next players turn\n")
        message)))
  
  )

; Create a game object
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
        alive-players (make-queue num-of-players)
        ]
    (Game. board players game-settings game-state alive-players)
    )
  )

