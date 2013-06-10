(ns us.dreisbach.we-owe.system
  (:require [us.dreisbach.we-owe.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn system
  "Returns a new instance of the application."
  []
  (let [db (atom {})
        handler (app db)]
    {:db db
     :handler handler
     :server (run-jetty handler {:port 8080 :join? false})}))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (.start (:server system))
  system)

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (.stop (:server system))
  system)
