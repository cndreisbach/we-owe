(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [us.dreisbach.we-owe.views :as views]))

(defn create-routes [db]
  (routes
   (GET "/" [] (views/index-page db))
   (GET "/:person" [person] (views/person-page db person))
   (route/resources "/")
   (route/not-found "Page not found")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site))
