(ns us.dreisbach.we-owe.client
  (:require [us.dreisbach.we-owe.views.templates :as templates]
            [goog.json :as gjson]
            [goog.dom.forms :as forms]
            [dommy.core :as dom]
            [ajax.core :refer [GET POST]])
  (:use-macros [dommy.macros :only [node sel1]]))

(declare init-add-debt-form)

(defn- add-debt-form
  [debt errors]
  (node (templates/add-debt-form :debt debt :errors errors)))

(defn- add-debt-success
  [{:keys [ok debt errors location]}]
  (if ok
    (set! (.-location js/window) location)
    (init-add-debt-form debt errors)))

(defn- add-debt-error [error-map]
  (.log js/console error-map))

(defn- add-debt-submit
  [event]
  (.preventDefault event)
  (let [form (forms/getFormDataMap (sel1 :#add-debt-form))
        params {:amount (first (.get form "amount"))
                :from (first (.get form "from"))
                :to (first (.get form "to"))}]
    (POST "/debts/add.json"
          {:format :json
           :keywordize-keys true           
           :params params
           :handler add-debt-success
           :error-handler add-debt-error})))

(defn- init-add-debt-form
  [debt errors]
  (dom/replace-contents! (sel1 :#add-debt-container)
                         (add-debt-form debt errors))
  (dom/listen! (sel1 :#add-debt-form) :submit add-debt-submit))

(defn main
  []
  (dom/listen! (sel1 :#add-debt-btn)
               :click
               (fn [event]
                 (.preventDefault event)
                 (init-add-debt-form {} {}))))

(main)
