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

(defn count-trop []
  (fetch-count :tropy))

(defn get-trop [id]
  (fetch-by-id :tropy (object-id id)))

(defn get-random-trop []
  (let [skip (rand-int (fetch-count :tropy))]
    (first (fetch :tropy :skip skip :limit 1))))

(defn update-trop [trop]
  )

(defn create-trop [title content]
  (insert! :tropy
           {:title title
            :content content}))

(defmacro page [title & body-content]
  `(html5
    [:head
     [:title ~title]
     (include-js "http://code.jquery.com/jquery-1.6.min.js")
     ]
    [:body
     ~@body-content]))

(defn init-page [req]
  (page "tropyclj"
        [:h1 "tropy"]
        [:p "Welcome to tropyclj!"]
        [:p "There are no pages yet. Why don't you create new page?"]
        [:a {:href "/new"} "Create new page"]))

(defn show-page [req]
  (let [trop (if-let [id (get-in req [:params :id])]
               (get-trop id)
               (get-random-trop))]
    (page "tropyclj - show"
          [:h1 (:titlte trop)]
          [:p (:content trop)]
          [:a {:href "/"} "other page"]
          " "
          [:a {:href "/edit"} "edit"]
          " "
          [:a {:href "/new"} "Create new page"])))

(defn show-or-init-page [req]
  (if (= 0 (count-trop))
    (init-page req)
    (show-page req)))

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
                 (label :title "title")
                 (text-field :title)
                 [:br]
                 (label :content "content")
                 [:br]
                 (text-area :content)
                 [:br]
                 (submit-button "trop!"))))

(defn create-page [{params :params}]
  (let [trop (create-trop (:title params)
                          (:content params))]
    (redirect (str "/show/" (:_id trop)))))

(defroutes tropyclj
  (GET "/" _ show-or-init-page)
  (GET "/show/:id" _ show-page)
  (GET "/new" _ new-page)
  (POST "/create" _ create-page)
  (GET "/edit" _ edit-page)
  (POST "/write" _ write-page)
  (not-found "not found"))

(wrap! tropyclj (:charset "utf8"))
(wrap! tropyclj (:reload '(tropyclj.core)))
(wrap! tropyclj (:stacktrace))
(wrap! tropyclj site)

(defn -main []
  (mongo-init)
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'tropyclj {:port port})))
