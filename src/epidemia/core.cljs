(ns epidemia.core
  (:require [clojure.browser.repl :as repl]
            [goog.dom :as dom]
            [goog.events :as events]
            ))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(def board-size 15)
(def cell-px-size 48)

(defn print-mouse-pos
  [event]
  (let [
        canvas (.getElementById js/document "game_board")
        x (- (.-offsetX event) (.-offsetLeft canvas)) 
        y (- (.-offsetY event) (.-offsetTop canvas)) 
        ]
    (println (+ "In pixels: " "X: " (str x) " Y: " (str y)) )
    (let [
          x-cell (quot x cell-px-size)
          y-cell (quot (- (* board-size cell-px-size) y) cell-px-size)
          ]
      (println (+ "In cells " "X: " (str x-cell) " Y: " (str y-cell)))
      )
    )
  )

(let [canvas (.createElement js/document "canvas") 
      body (.getElementById js/document "body1")
      div (.createElement js/document "div")
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
  (.appendChild body div)
  (events/listen canvas "mousedown" print-mouse-pos)
  )
