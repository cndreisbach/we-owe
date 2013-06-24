(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [noir.response :as response]
            [noir.util.route :refer [restricted]]
            [noir.util.middleware :refer [app-handler]]
            [noir.session :as session]
            [us.dreisbach.we-owe.views :as views]
            [us.dreisbach.we-owe.resources :as resources]))

(defn- logged-in? [request]
  (session/get :user))

(defn create-routes [db]
  (routes
   (GET "/" [] (response/redirect "/debts" :permanent))

   (GET "/debts" [] resources/debts)
   (ANY "/debts/add" [] (restricted resources/add-debt))

   (POST "/add-debt.json" {body :body} (views/add-debt-json db (slurp body)))   

   (GET "/user/:person.json" [person] (restricted (views/person-json db person)))
   (GET "/user/:person" [person] (restricted (views/person-page db person)))
   
   (GET "/login" [] (views/login-page))
   (POST "/login" [username password]
         (views/login-post db {:username username :password password}))
   (ANY "/logout" [] (views/logout-page))

   (GET "/*.css" {{path :*} :route-params} (views/css-page path))   
   
   (route/resources "/")
   (route/not-found "Page not found")))

(defn wrap-db [db handler]
  (fn [{:as request}]
    (-> request
        (assoc :db db)
        handler)))

(defn create-handler [db]
  (wrap-db db
   (app-handler
    [(create-routes db)]
    :access-rules [{:redirect "/login"
                    :rules [logged-in?]}])))
