(ns epidemia.gamestate)

(defprotocol IGameState
  (get-active-player [this])
  (set-active-player [this new-player])
  (get-steps-left [this])
  (set-steps-left [this new-steps])
  (dec-steps [this])
  )

(deftype GameState [active-player steps-left]
  IGameState
  (get-active-player [this] active-player)
  (get-steps-left [this] steps-left)
  (set-active-player [this new-player] (set! (.-active-player this) new-player))
  (set-steps-left [this new-steps] (set! (.-steps-left this) new-steps))
  (dec-steps [this] (let [new-steps (dec (.-steps-left this))]
                     (set-steps-left this new-steps)))
  )
