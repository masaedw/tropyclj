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
  (:use ring.util.response)
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

(defn show-page [req]
  (page "tropyclj - show"
        [:h1 "tropy"]
        [:p "hello from clojure"]
        [:a {:href "/edit"} "edit"]
        " "
        [:a {:href "/new"} "new"]))

(defn edit-page [req]
  (page "tropyclj - edit"
        (form-to [:post "/write"]
                 (text-field :title "title")
                 [:br]
                 (text-area :body "aaaa")
                 [:br]
                 (submit-button "trop!"))))

(defn write-page [req]
  (redirect "/"))

(defn new-page [req]
  (page "tropyclj - new"
        (form-to [:post "/create"]
                 (text-field :title "new title")
                 [:br]
                 (text-area :body "new body")
                 [:br]
                 (submit-button "trop!"))))

(defn create-page [req]
  (redirect "/"))

(defroutes tropyclj
  (GET "/" _ show-page)
  (GET "/new" _ new-page)
  (POST "/create" _ create-page)
  (GET "/edit" _ edit-page)
  (POST "/write" _ write-page)
  (not-found "not found"))

(wrap! tropyclj (:charset "utf8"))
(wrap! tropyclj (:reload '(tropyclj.core)))
(wrap! tropyclj (:stacktrace))

(defn -main []
  (mongo-init)
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'tropyclj {:port port})))
