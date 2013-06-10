(ns us.dreisbach.we-owe.handler)

(defn body [count]
  (str "Hi world! " count))

(defn app [db]
  (fn [req]
    {:body (body (count @db)) :headers {} :status 200}))

(comment
  (handler {:uri "/"})
  )
