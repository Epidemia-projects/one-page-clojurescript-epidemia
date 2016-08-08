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

(let [canvas (.createElement js/document "canvas") 
      body (.getElementById js/document "body1")
      div (.createElement js/document "div")
      boardpxsize (* cell-px-size board-size)
      ]
  (.setAttribute canvas "id" "myCanvas")
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
  )
