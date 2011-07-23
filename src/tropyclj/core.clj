(ns tropyclj.core
  (:use ring.adapter.jetty)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace)
  (:use somnium.congomongo)
  (:use tropyclj.mongo-init)
)

(mongo-init)

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Clojure!\n"})

(def app
     (-> #'handler
         (wrap-reload '(tropyclj.core))
         (wrap-stacktrace)))

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'app {:port port})))
