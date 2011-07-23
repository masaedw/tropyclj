(ns tropyclj.core
  (:use clojure.contrib.pprint)
  (:use compojure.core)
  (:use compojure.handler)
  (:use compojure.route)
  (:use ring.adapter.jetty)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace)
  (:use somnium.congomongo)
  (:use tropyclj.middleware)
  (:use tropyclj.mongo-init)
)

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Clojure!\n"})

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
