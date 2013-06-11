(ns us.dreisbach.we-owe.views
  (:require [clojure.pprint :refer [pprint]]))

(defn index-page [db]
  (str "Hi world! " (with-out-str (pprint @db))))

(defn key-page [db key]
  (let [key (keyword key)
        value (key @db)]
    (if value
      (str "Key: " (name key) "\nValue: " (with-out-str (pprint value))))))

