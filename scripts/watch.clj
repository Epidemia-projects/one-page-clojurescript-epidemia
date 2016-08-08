(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'epidemia.core
   :output-to "out/epidemia.js"
   :output-dir "out"})
