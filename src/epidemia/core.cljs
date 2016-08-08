(ns epidemia.core
  (:require [clojure.browser.repl :as repl]
            [goog.dom :as dom]
            [goog.events :as events]
            [epidemia.logic :as logix] 
            ))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(def board-size logix/board-size)
(def cell-px-size 48)

(defn handle-mouse-click [x-cell y-cell]
  (println (+ "X: " x-cell " Y: " y-cell))
  (if (= "true" (logix/make-step [x-cell y-cell])) 
    (doall
      (let [
        x-top-left (* x-cell cell-px-size)
        y-top-left (* (- board-size y-cell 1) cell-px-size)
        x-bottom-right (+ x-top-left cell-px-size)
        y-bottom-right (+ y-top-left cell-px-size)
        canvas (.getElementById js/document "game_board")
        ]
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
    (println (logix/return-neighbor-coords [x-cell y-cell]))
    (let [cell1 (logix/get-cell [x-cell y-cell])]
     (println (str (:status cell1))))  
    )     
    (println "Impossible to make step into this cell")
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
  (logix/init-game )
  (events/listen canvas "mousedown" print-mouse-pos)
  )
