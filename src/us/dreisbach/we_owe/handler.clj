(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [us.dreisbach.we-owe.views :as views]))

(defn create-routes [db]
  (routes
   (GET "/" [] (views/index-page db))
   (GET "/:key" [key] (views/key-page db key))
   (route/not-found "Page not found")))

(defn wrap-plain-text
  [handler]
  (fn [req]
    (assoc-in (handler req) [:headers "Content-Type"] "text/plain;charset=UTF-8")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site
      wrap-plain-text))
