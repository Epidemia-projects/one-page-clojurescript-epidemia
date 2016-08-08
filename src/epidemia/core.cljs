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

(defn handle-mouse-click [x-cell y-cell]
  (println (+ "X: " x-cell " Y: " y-cell))
  (let [
        x-top-left (* x-cell cell-px-size)
        y-top-left (* (- board-size y-cell 1) cell-px-size)
        x-bottom-right (+ x-top-left cell-px-size)
        y-bottom-right (+ y-top-left cell-px-size)
        canvas (.getElementById js/document "game_board")
        ]
    (println (+ "This cell top left corner X:" (str x-top-left) " Y: " (str y-top-left)))
    (println (+ "This cell bottom right corner X:" (str x-bottom-right) " Y: " (str y-bottom-right)))
    (let [
          context (.getContext canvas "2d")
          cross-img (.createElement js/document "img")
          ]
      (aset cross-img "src" "img/cross.png")
      (aset cross-img "onload" (fn [] (dorun
                              (.drawImage context cross-img x-top-left y-top-left)
                              )))
      )
    )
  )

(defn print-mouse-pos
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
