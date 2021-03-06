(ns epidemia.core
  (:require [clojure.browser.repl :as repl]
            [goog.dom :as dom]
            [goog.events :as events]
            [epidemia.game :as gm] 
            [epidemia.coord :as crd]
            ))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))
;;

(enable-console-print!)

; Define game constants
(def board-size 9)
(def num-of-players 2)
(def steps-per-move 3)
(def cell-px-size 48)
;(def current-player 0)

(defn compose-img-src
  "Determines what image should be drawn onto canvas"
  [details]
  (let [player (:player details)
        status (:status details)
        ]
    (if (= status :crossed)
      (+ "img/cross" (str player) ".png")
      (+ "img/fill" (str player) ".png")
      )
    )
  )
;

(defn handle-mouse-click [x-cell y-cell]
  "Receive mouse click coordinates and decide which player make step into what cell"
  (def mouse-click-crd (crd/Coord. x-cell y-cell))
  (if (gm/can-make-step? game mouse-click-crd) 
    ( let [
        x-top-left (* x-cell cell-px-size)
        y-top-left (* (- board-size y-cell 1) cell-px-size)
        x-bottom-right (+ x-top-left cell-px-size)
        y-bottom-right (+ y-top-left cell-px-size)
        canvas (.getElementById js/document "game_board")
        context (.getContext canvas "2d")
        details (gm/get-step-details game mouse-click-crd)
        img-src (compose-img-src details)
        cross-img (.createElement js/document "img")
        game-log (.getElementById js/document "game_log")
        log-text (.-value game-log)
        new-message (gm/make-step-return-message game mouse-click-crd)
        ]
        ()
        (aset game-log "value" (+ log-text new-message))    
        (aset game-log "scrollTop" (aget game-log "scrollHeight"))
        (aset cross-img "src" img-src)
        (aset cross-img "onload" (fn [] (dorun
                              (.drawImage context cross-img x-top-left y-top-left)
                              ))))
    )
  )

(defn print-mouse-pos
  "Get coordinates of mouse click and bypass it to handle-mouse-click function"
  [event]
  (let [
        canvas (.getElementById js/document "game_board")
        x (- (.-offsetX event) (.-offsetLeft canvas)) 
        y (- (.-offsetY event) (.-offsetTop canvas)) 
        x-cell (quot x cell-px-size)
        y-cell (quot (- (* board-size cell-px-size) y) cell-px-size)
        ]
    (handle-mouse-click x-cell y-cell) 
    )
  )

; Initialize the game board
(let [canvas (.createElement js/document "canvas") 
      body (.getElementById js/document "body1")
      div (.getElementById js/document "column1")
      boardpxsize (* cell-px-size board-size)
      ]
  (.setAttribute canvas "id" "game_board")
  (.setAttribute canvas "width" (str boardpxsize))
  (.setAttribute canvas "height" (str boardpxsize))
  (.setAttribute canvas "style" "border:none;") 
  (let [context (.getContext canvas "2d")
        img (.createElement js/document "img")
        ]
    (aset img "src" "img/cell.png")
    (aset img "alt" "alt")
    (aset img "onload" (fn [] (dorun
                               (for [x (range board-size)
                                     y (range board-size)
                                     ]
                                (.drawImage context img (* x cell-px-size) (* y cell-px-size))
                                 ) 
                               )
                         ))
    )
  (.appendChild div canvas)
  ;(.appendChild body div)
  (def game (gm/init-game {:board-size board-size :number-of-players num-of-players :steps-per-move steps-per-move}))
  (events/listen canvas "mousedown" print-mouse-pos)
  (aset body "onload"  (fn []
                         (let [game-log (.getElementById js/document "game_log") ]
                           (aset game-log "value" "---Game Log---\n"))))
  )
