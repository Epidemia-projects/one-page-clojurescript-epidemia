(ns epidemia.gamestate)

(defprotocol IGameState
  "An object to contain information about state of the game"
  (get-active-player [this] "Return active player")
  (set-active-player [this new-player] "Mute active player")
  (get-steps-left [this] "Return how many steps are left for thew current player")
  (set-steps-left [this new-steps] "Set number of steps left")
  (dec-steps [this] "Decrease by one number of steps left for a current player")
  )

; Implementation of IGameState
(deftype GameState [active-player steps-left]
  IGameState
  (get-active-player [this] active-player)
  (get-steps-left [this] steps-left)
  (set-active-player [this new-player] (set! (.-active-player this) new-player))
  (set-steps-left [this new-steps] (set! (.-steps-left this) new-steps))
  (dec-steps [this] (let [new-steps (dec (.-steps-left this))]
                     (set-steps-left this new-steps)))
  )
