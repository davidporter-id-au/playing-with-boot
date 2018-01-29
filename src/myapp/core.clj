(ns myapp.core
  (:gen-class)
  (:require
   [clj-http.client :as client]
   [clojure.core.async :as async]
   ))


(def api-key (System/getenv "API_KEY"))
(def host (System/getenv "HOST"))

(defn get-page [page]
  (:body
    (client/get
     (str host "/api/v2/users?per_page=100&page=" page)
     { :headers { :authorization (str "Bearer " api-key) }
      :as :json })))


(defn get-all [push-chan completion-chan]
  (loop [
         page 0
         result (get-page page)
         ]

    (if (empty? result)
      (async/>!! completion-chan "done")

      (do
        (async/>!! push-chan result)
        (print "batch" page)
        (recur (+ 1 page) (get-page (+ 1 page)))
        ))))

(def users-queue (async/chan 500))
(def completion (async/chan 4))


(defn process [users-chan]
  (loop [batch (async/<!! users-chan)]
    (println (map :email batch))
    (recur (async/<!! users-chan)))
  )

(defn -main
  [& args]
  (println "starting")
  (future (get-all users-queue completion))
  (future (process users-queue))
  (async/<!! completion)
  (println "done"))
