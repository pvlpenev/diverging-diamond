(ns diverging-diamond.core
  (:use [diverging-diamond.db-layer :as db]
	[diverging-diamond.html :as html]
	[ring.adapter.jetty :only [run-jetty]]
        [compojure.core])
  (:require [compojure.route :as route]))

(defn home []
  (let [links (db/get-links)]
    (layout "Home"
	    (for [link links]
	       [:div (:id link) [:a {:href (:url link)} (:title link)]]))))

(defroutes ddroutes
  (GET "/" [] (home))
  (route/files "/")
  (route/not-found "<h1>Not Found</h1>"))


(defn start []
  (run-jetty (var ddroutes) {:port 8080
                           :join? false}))