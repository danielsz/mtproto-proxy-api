(ns mtproto-api.core
  (:gen-class)
  (:require [system.repl :refer [set-init! go]]
            [mtproto-api.system :refer [prod]]))

(defn -main
  "Start a production system."
  [& args]
  (if-let [system (first args)]
    (do (require (symbol (namespace (symbol system))))
        (set-init! (resolve (symbol system))))
    (set-init! #'prod))
  (go))

