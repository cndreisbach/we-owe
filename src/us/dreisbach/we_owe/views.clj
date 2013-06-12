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
   (html5 [:head]
          [:body
           content])))

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
     [:pre
      (str "You owe:\n" (pstr owes)
           "\n\nYou are owed:\n" (pstr owed))])))

