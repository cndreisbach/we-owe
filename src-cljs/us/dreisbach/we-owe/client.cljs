(ns us.dreisbach.we-owe.client
  (:require [us.dreisbach.we-owe.views.templates :as templates]
            [dommy.core :as dom])
  (:use-macros [dommy.macros :only [node sel1]]))

(defn- add-debt-form
  [debt errors]
  (node (templates/add-debt-form :debt debt :errors errors)))

(defn- add-debt-submit
  [event]
  (.preventDefault event)
  (js/alert "whoops!"))

(defn main
  []
  (dom/listen! (sel1 :#add-debt-btn)
               :click
               (fn [event]
                 (.preventDefault event)
                 (dom/replace-contents! (sel1 :#add-debt-container)
                                        (add-debt-form {} {}))
                 #_(dom/listen! (sel1 :#add-debt-form) :submit add-debt-submit))))

(main)
