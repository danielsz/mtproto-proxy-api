(ns mtproto-api.handler
  (:require [reitit.ring :as r]
            [ring.util.response :refer [response content-type status]]
            [clojure.tools.logging :as log])
  (:import [clojure.lang ExceptionInfo]))


(defn mtproto-api [arg]
  (let [pb (ProcessBuilder. ["/opt/mtp_proxy/bin/mtp_proxy" "eval" arg])
        process (.start pb)
        rc (.waitFor process)
        rv (-> (.getInputStream process)
              slurp)]
    (if (= rc 0)
      rv
      (throw (ex-info "Error" {:output rv})))))

(defn api [command]
  (try (let [x (mtproto-api command)]
         (-> (response x)
            (content-type  "text/plain")))
       (catch ExceptionInfo e (-> (response (:output (ex-data e)))
                                 (status 400)
                                 (content-type  "text/plain")))))

(defn ring-handler [_]
  (r/ring-handler
   (r/router
    [["/api/connections" {:get (fn [_]
                                 (api "lists:sum([proplists:get_value(all_connections, L) || {_, L} <- ranch:info()])."))}]
     ["/api/add" {:put (fn [{{:keys [domain]} :params}]
                         (api (str "mtp_policy_table:add(customer_domains, tls_domain, \"" domain "\").")))}]
     ["/api/delete" {:delete (fn [{{:keys [domain]} :params}]
                               (api (str "mtp_policy_table:del(customer_domains, tls_domain, \"" domain "\").")))}]
     ["/api/exists" {:get (fn [{{:keys [domain]} :params}]
                            (api (str "mtp_policy_table:exists(customer_domains, mtp_policy:convert(tls_domain, \""  domain  "\")).")))}]])))
