(ns us.dreisbach.we-owe-test
  (:use clojure.test
        us.dreisbach.we-owe))

(deftest simplify-test
  (testing "test"      
    (is (= {["alice" "bob"] 5.0}
             (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}])))
    (is (= {["alice" "bob"] 5.0
            ["doug" "claire"] 3.0}
           (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "claire" :to "doug" :amount 3.0 :when #inst "2013-01-03"}])))
    (is (= {["alice" "bob"] 7.0}
           (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "bob" :to "alice" :amount 2.0 :when #inst "2013-01-03"}])))
    (is (= {["alice" "bob"] 2.0}
           (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}])))
    (is (= {["bob" "alice"] 2.0}
           (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "alice" :to "bob" :amount 7.0 :when #inst "2013-01-03"}])))))

(deftest balances-test
  (testing "test"
    (is (= {"alice" -5.0
            "bob" 5.0}
           (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}])))
    (is (= {"alice" -5.0
            "bob" 5.0
            "doug" -3.0
            "claire" 3.0}
           (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "claire" :to "doug" :amount 3.0 :when #inst "2013-01-03"}])))
    (is (= {"alice" -7.0
            "bob" 7.0}
           (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "bob" :to "alice" :amount 2.0 :when #inst "2013-01-03"}])))
    (is (= {"alice" -2.0
            "bob" 2.0}
           (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                      {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}])))))
