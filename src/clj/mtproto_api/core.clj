(ns mtproto-api.core
  (:gen-class)
  (:require [system.repl :refer [set-init! go]]
            [jvm-utils.core :as jvm]
            [mtproto-api.system :refer [prod]]))

(defn -main
  "Start a production system."
  [& args]
  (jvm/merge-properties)
  (set-init! #'prod)
  (go))

