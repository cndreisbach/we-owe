(ns us.dreisbach.we-owe.handler
  (:require [clojure.pprint :refer [pprint]]))

(defn body [obj]
  (str "Hi world! " (with-out-str (pprint obj))))

(defn app [db req]
  {:body (body @db) :headers {} :status 200})
