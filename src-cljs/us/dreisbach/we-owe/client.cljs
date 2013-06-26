(ns us.dreisbach.we-owe.client
  (:require [us.dreisbach.we-owe.views.templates :as templates])
  (:use-macros [dommy.macros :only [node]]))

(defn add-debt-form [debt errors]
  (node (templates/add-debt-form :debt debt :errors errors)))
