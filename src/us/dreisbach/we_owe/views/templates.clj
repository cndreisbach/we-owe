(ns us.dreisbach.we-owe.views.templates)

(defn- horizontal-input
  [field label type value errors]
  (let [field (name field)
        field-id (str field "-field")]
    [:div {:class (if (seq errors) "control-group error" "control-group")}
     [:label.control-label {:for field-id} label]
     [:div.controls
      [:input {:id field-id :type type :name field :value value}]
      (if (seq errors)
        (for [error errors]
          [:span.help-block error]))]]))

(defn output-form
  [fields & {:keys [values errors] :or [values {} errors {}]}]
  (for [[field label] fields]
    (let [type (if (= field :password) "password" "text")]
      (horizontal-input field label type (field values) (field errors)))))

(defn add-debt-form
  [& {:keys [debt errors] :or [debt {} errors {}]}]
  [:div
   [:h1 "Add a debt"]
   [:form#add-debt-form.form-horizontal {:method "POST" :action "/debts/add"}
    (output-form [[:from "Lender"]
                  [:to "Borrower"]
                  [:amount "Amount"]]
                 :values debt
                 :errors errors)
    [:div.control-group
     [:div.controls
      [:button.btn.btn-primary {:type "submit"} "Add a debt"]]]]])

