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
      (str "Key: " (name key) "\nValue: " (with-out-str (pprint value))))))

(defn create-routes [db]
  (routes
   (GET "/" [] (index-page db))
   (GET "/:key" [key] (key-page db key))
   (route/not-found "Page not found")))

(defn wrap-plain-text
  [handler]
  (fn [req]
    (assoc-in (handler req) [:headers "Content-Type"] "text/plain;charset=UTF-8")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site
      wrap-plain-text))
