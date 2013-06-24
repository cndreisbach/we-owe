(ns us.dreisbach.we-owe.resources
  (:require [liberator.core :refer [defresource]]
            [ring.util.response :refer [redirect-after-post]]
            [validateur.validation :refer :all]            
            [us.dreisbach.we-owe.views :as views]
            [us.dreisbach.we-owe.debts :as debts]))

(defresource debts
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [{:keys [request representation]}]
               (let [db (:db request)
                     original-debts (:debts @db)
                     debts (debts/simplify original-debts)
                     balances (debts/balances original-debts)]
                 (views/debts {:format (:media-type representation)
                               :debts debts
                               :balances balances}))))

(defresource add-debt
  :allowed-methods [:get :post]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [ctx]
               (views/add-debt-page (:debt ctx {}) (:errors ctx {})))
  :post! (fn [{:keys [request representation]}]
           (let [db (:db request)
                 {:keys [from to amount]} (:params request)
                 debt {:from from, :to to, :amount amount}                 
                 debt-validator (validation-set
                                 (presence-of :from)
                                 (presence-of :to)
                                 (presence-of :amount)
                                 (format-of :amount :format #"^\d+(\.\d+)?$" :message "must be a number"))]
               (if (valid? debt-validator debt)
                 (let [debt (update-in debt [:amount] #(Float/parseFloat %))]
                   (swap! db update-in [:debts] conj debt)
                   {:success true, :location "/debts"})
                 {:success false, :debt debt, :errors (debt-validator debt)})))
  :post-redirect? (fn [{:keys [success location]}]
                    (if (and success location)
                      {:location location}))
  :new? :success
  :respond-with-entity? true)
