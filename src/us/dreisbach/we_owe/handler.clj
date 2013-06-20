(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as response]
            [us.dreisbach.we-owe.views :as views]))

(defn create-routes [db]
  (routes
   (GET "/" [] (response/redirect "/debts"))
   (GET "/debts" [] (views/index-page db))
   (GET "/debts.json" [] (views/index-json db))
   (GET "/add-debt" [] (views/add-debt-page))
   (POST "/add-debt" [from to amount] (views/add-debt-post db {:from from :to to :amount amount}))
   (POST "/add-debt.json" {body :body} (views/add-debt-json db (slurp body)))   
   (GET "/:person.json" [person] (views/person-json db person))
   (GET "/:person" [person] (views/person-page db person))
   (GET "/*.css" {{path :*} :route-params} (views/css-page path))   
   (route/resources "/")
   (route/not-found "Page not found")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site))
