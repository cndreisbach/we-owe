(ns us.dreisbach.we-owe.views
  (:require [clojure.pprint :refer [pprint]]
            [us.dreisbach.we-owe.debts :refer [simplify balances]]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all]))

(defn- pstr [obj]
  (with-out-str (pprint obj)))

(defn- layout
  [& content]
  (html
   (html5 [:head
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
           (include-css "/css/bootstrap.min.css")]
          [:body
           [:div {:class "container"}
            content]
           (include-js "/js/bootstrap.min.js")])))

(defn index-page [db]
  (let [debts (:debts @db)]
    (layout
     [:h1 "Debts"]
     [:ul
      (for [[[debtor lender] amount] (simplify debts)]
        [:li (str debtor " owes " lender " $" amount ".")])]
     [:h1 "Balances"]
     [:ul
      (for [[person amount] (balances debts)]
        [:li (str person ": $" amount)])])))

(defn person-page [db person]
  (let [debts (simplify (:debts @db))
        owed (->> debts
                  (filter (fn [[[_ owed] amount]]
                            (= owed person)))
                  (map (fn [[[owes _] amount]] (vector owes amount))))
        owes (->> debts
                  (filter (fn [[[owes _] amount]]
                            (= owes person)))
                  (map (fn [[[_ owed] amount]] (vector owed amount))))]
    (layout
     [:h1 "You owe:"]
     [:ul
      (if (zero? (count owes))
        [:li "Nothing!"]
        (for [[person amount] owes]
          [:li (str person ": $" amount)]))]
     [:h1 "You are owed:"]
     [:ul
      (if (zero? (count owed))
        [:li "Nothing!"]
        (for [[person amount] owed]
          [:li (str person ": $" amount)]))])))

