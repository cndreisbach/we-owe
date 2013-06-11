(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defn body [obj]
  (str "Hi world! " (with-out-str (pprint obj))))

(defn index-page [db]
  (body @db))

(defn key-page [db key]
  (let [key (keyword key)
        value (key @db)]
    (if value
      (str "Key: " (name key) "<br />Value: " (with-out-str (pprint value))))))

(defn create-routes [db]
  (routes
   (GET "/" [] (index-page db))
   (GET "/:key" [key] (key-page db key))
   (route/not-found "Page not found")))

(defn create-handler [db]
  (handler/site (create-routes db)))
