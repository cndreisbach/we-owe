(ns us.dreisbach.we-owe.views
  (:require [clojure.pprint :refer [pprint]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.java.io :as io]            
            [ring.util.response :refer [redirect-after-post]]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :as form]
            [validateur.validation :refer :all]
            [cheshire.core :as json]
            [garden.core :refer [css]]
            [noir.response :as response]
            [noir.session :as session]
            [us.dreisbach.we-owe.debts :as debts :refer [valid-debt? all-users]]
            [us.dreisbach.we-owe.views.templates :refer [add-debt-form output-form]]))

(defn- pstr
  [obj]
  (with-out-str (pprint obj)))

(defn- user-link
  [user]
  [:a {:href (str "/user/" user)} user])

(defn- current-user [] (session/get :user))

(defn- login-nav
  []
  (let [user (session/get :user)]
    (if user
      [:ul.nav.pull-right
       [:li.divider-vertical]                   
       [:li [:a {:href (str "/user/" user)}
             (str "Logged in as " user)]]
       [:li.divider-vertical]
       [:li [:a {:href "/logout"} "Logout"]]]
      [:ul.nav.pull-right
       [:li.divider-vertical]                   
       [:li [:a {:href "/login"} "Login"]]])))

(defn- layout
  [& content]
  (html
   (html5 [:head
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
           (include-css "/css/bootstrap.min.css")
           (include-css "/css/style.css")
           [:style {:type "text/css"} "body { margin-top: 30px; }"]]
          [:body
           [:div.container
            [:div.navbar
             [:div.navbar-inner
              [:a.brand {:href "/debts"} "WeOwe"]
              (login-nav)]]
            content]
           (include-js "/js/bootstrap.min.js")
           (include-js "/js/main.js")])))

(defmulti debts :format)

(defmethod debts "text/html"
  [{:keys [debts balances]}]
  (layout
   [:h1 "Debts"]
   [:ul
    (for [[[debtor lender] amount] debts]
      [:li (user-link debtor) (str " owes " lender " $" amount ".")])]
   [:h1 "Balances"]
   [:ul
    (for [[person amount] balances]
      [:li (user-link person) (str ": $" amount)])]
   (if (current-user)
     [:div#add-debt-container
      [:a#add-debt-btn.btn.btn-primary {:href "/debts/add"} [:i.icon-plus.icon-white] " Add a debt"]]
     [:div#add-debt-container])))

(defmethod debts "application/json"
  [{:keys [debts balances]}]
  (let [debts (map (fn [[[to from] amount]]
                     {:lender from :debtor to :amount amount})
                   debts)]
    {:debts debts :balances balances}))

(defmulti user :format)

(defmethod user "text/html"
  [{:keys [debts owed owes user]}]
  (layout
   [:h1 (str user " owes:")]
   [:ul
    (if (zero? (count owes))
      [:li "Nothing!"]
      (for [[user amount] owes]
        [:li (user-link user) (str ": $" amount)]))]
   [:h1 (str user " is owed:")]
   [:ul
    (if (zero? (count owed))
      [:li "Nothing!"]
      (for [[user amount] owed]
        [:li (user-link user) (str ": $" amount)]))]))

(defmethod user "application/json"
  [{:keys [debts owed owes user]}]
  {:debts owes :loans owed})

(defmulti add-debt :format)

(defmethod add-debt "text/html"
  [{:keys [debt errors]}]
  (layout (add-debt-form :debt debt :errors errors)))

(defmethod add-debt "application/json"
  [{:keys [debt errors] :or [debt {} errors {}]}]
  (if (empty? errors)
    {:ok true :debt debt}
    {:ok false :debt debt :errors errors}))

(defn login-page
  ([] (login-page {} {}))
  ([credentials errors]
     (layout
      [:h1 "Login"]
      (form/form-to {:class "form-horizontal"} [:post "/login"]
                    (output-form [[:username "Name"]
                                  [:password "Password"]]
                                 :values credentials
                                 :errors errors)
                    [:div.control-group
                     [:div.controls
                      [:button.btn.btn-primary {:type "submit"} "Login"]]]))))

(defn login-post
  [db credentials]
  (let [legal-users (all-users (:debts @db))
        login-validator (validation-set
                         (presence-of :username)
                         (inclusion-of :username :in legal-users)
                         (presence-of :password)
                         (length-of :password :within (range 4 100)))]
    (if (valid? login-validator credentials)
      (do
        (session/put! :user (:username credentials))
        (redirect-after-post "/debts"))
      (let [errors (login-validator credentials)]
        (login-page credentials errors)))))

(defn logout-page
  []
  (session/remove! :user)
  (redirect-after-post "/debts"))

(defn css-page [path]
  (when-let [garden-url (io/resource (str "public/" path ".garden"))]
    (let [garden-data (load-file (.getPath garden-url))]
      {:status 200
       :headers {"Content-Type" "text/css"}
       :body (css garden-data)})))

(def css-page-memo (memoize css-page))
