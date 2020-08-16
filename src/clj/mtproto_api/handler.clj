(ns mtproto-api.handler
    (:require [reitit.ring :as r]))

(defn ring-handler [_]
  (r/ring-handler
   (r/router
    [["/" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "text/html"}
                   :body "<html>Hello World</html>"})}]])
   (r/routes
    (r/create-resource-handler {:path "/" :root ""}))))
