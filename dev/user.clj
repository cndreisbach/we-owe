(ns user
  (:require [alembic.still :refer [distill]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [expectations :refer [run-tests run-all-tests]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [us.dreisbach.we-owe.system :as system]
            [us.dreisbach.we-owe.debts :as debts]))

(def system nil)

(defn init
  "Constructs the current dev system."
  []
  (alter-var-root #'system
                  (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system system/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (system/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(comment
  (def debt {:from "clinton" :to "pete" :amount 3.50})
  (def debt {:from "clinton" :to "diego" :amount 2.00})
  (def debt {:from "pete" :to "clinton" :amount 1.25})
  (swap!
   (:db system)
   update-in [:debts] debts/add-debt debt))
