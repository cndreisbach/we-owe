(ns us.dreisbach.we-owe
  (:require [clojure.test :refer :all]))

(defn- transform-debts-to-vecs
  [debts]
  (map #(vector [(:to %) (:from %)] (:amount %)) debts))

(defn- merge-debts
  "Merge debts where person=person and owes=owes."
  [debt-vecs]
  (reduce (fn [tot [people amount]]
            (update-in tot [people] (fnil + 0) amount)) {} debt-vecs))

(defn- cancel-out-debts
  "Cancel out debts where person=owes and owes=person."
  [debt-map]
  (reduce (fn [tot [[person owes] amount]]
            (if-let [reciprocal-amount (get tot [owes person])]
              (let [new-amount (- amount reciprocal-amount)]
                (if (> new-amount 0)
                  (assoc (dissoc tot [owes person]) [person owes] new-amount)
                  (assoc tot [owes person] (- new-amount))))
              (assoc tot [person owes] amount)))
          {} debt-map))

(defn- check-debt-validity
  [f debts]
  {:pre [(every? identity
                 (map (comp #(not-any? nil? %)
                            (juxt :from :to :amount)) debts))]}
  (f debts))

(defn simplify
  "Take a vector of lending maps {:to a :from b :amount x :datetime dt} and simplify them to a vector of maps showing who owes who {:person a :owes b :amount x}."
  [debts]
  (-> debts
      transform-debts-to-vecs
      merge-debts
      cancel-out-debts))

(def simplify (partial check-debt-validity simplify))

(defn balances
  "Take a vector of lending maps and produce a map of balances. A positive balance is a credit, while a negative balance is a debt. All balances must total zero."
  [debts]
  {:post [(zero? (reduce (fn [x y] (+ x y)) (map second %)))]}
  (let [debts (simplify debts)]
    (reduce (fn [tot [[borrower lender] amount]]
              (-> tot
                  (update-in [borrower] (fnil - 0) amount)
                  (update-in [lender] (fnil + 0) amount)))
            {} debts)))

(def balances (partial check-debt-validity balances))
