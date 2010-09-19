(ns diverging-diamond.core
  (:use [ring.adapter.jetty :only [run-jetty]]
	[ring.util.response :only [redirect]]
        [compojure.core])
  (:require [compojure.route :as route]
	    [diverging-diamond.db-layer :as db]
	    [diverging-diamond.html :as html]))

(defroutes ddroutes
  (GET "/" [] (html/home))
  (GET "/add" [] (html/add-form))
  (POST "/add" [title url] (db/add-link-to-db title url)
	                   (redirect "/"))
  (route/files "/")
  (route/not-found "<h1>Not Found</h1>"))


(defn start []
  (run-jetty (var ddroutes) {:port 8080
                           :join? false}))