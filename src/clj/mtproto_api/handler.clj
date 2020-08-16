(ns mtproto-api.handler
  (:require [reitit.ring :as r]
            [clojure.tools.logging :as log]))

(defn wrap-debug
  [handler]
  (fn [request]
    (log/debug request)
    (handler request)))

(defn mtproto-api [arg]
  (let [pb (ProcessBuilder. ["/opt/mtp_proxy/bin/mtp_proxy" "eval" arg])
        process (.start pb)
        rc (.waitFor process)]
    (if (= rc 0)
      (-> (.getInputStream process)
         slurp)
      (throw (Exception. "there was an error while executing command")))))


(defn ring-handler [_]
  (r/ring-handler
   (r/router
    [["/api/connections" {:get (fn [_]
                                 {:status 200
                                  :headers {"Content-Type" "text/plain"}
                                  :body (mtproto-api "'lists:sum([proplists:get_value(all_connections, L) || {_, L} <- ranch:info()]).'"
)})}]])
   (r/routes
    (r/create-resource-handler {:path "/" :root ""}))))
