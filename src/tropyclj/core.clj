(ns tropyclj.core
  (:use clojure.contrib.pprint)
  (:use compojure.core)
  (:use compojure.handler)
  (:use compojure.route)
  (:use hiccup.core)
  (:use hiccup.form-helpers)
  (:use hiccup.page-helpers)
  (:use ring.adapter.jetty)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace)
  (:use somnium.congomongo)
  (:use tropyclj.middleware)
  (:use tropyclj.mongo-init)
)

(defmacro page [title & body-content]
  `(html5
    [:head
     [:title ~title]
     (include-js "http://code.jquery.com/jquery-1.6.min.js")
     ]
    [:body
     ~@body-content]))

(defn handler [req]
  (page "tropyclj"
        [:h1 "tropy"]
        [:p "hello from clojure"]))

(defroutes tropyclj
  (GET "/" req (handler req))
  (not-found "not found"))

(wrap! tropyclj (:charset "utf8"))
(wrap! tropyclj (:reload '(tropyclj.core)))
(wrap! tropyclj (:stacktrace))

(defn -main []
  (mongo-init)
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'tropyclj {:port port})))
