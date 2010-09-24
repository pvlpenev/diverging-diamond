(ns diverging-diamond.core
  (:use [ring.adapter.jetty :only [run-jetty]]
	[ring.util.response :only [redirect]]
        [compojure.core]
	[sandbar.auth :only [with-security any-role-granted? defauth]]
        [sandbar.form-authentication :only [form-authentication-routes form-authentication]]
        [sandbar.stateful-session :only [wrap-stateful-session]])
  (:require [compojure.route :as route]
	    [diverging-diamond.db-layer :as db]
	    [diverging-diamond.html :as html]
	    [diverging-diamond.forms :as forms]))


(def security-policy
  [#".*\.(css|js|png|jpg|gif)$" :any
   #"/login.*" :any
   #"/logout.*" :any
   #"/permission-denied.*" :any
   #"/register" :any
   #"/add" #{:user :admin}
   #"/" :any])

(defroutes droutes
  (GET "/" [] (html/home))
  (GET "/add" [] (forms/add-form))
  (POST "/add" [title url] (db/add-link-to-db title url)
	                   (redirect "/"))
  (GET "/register" [] (forms/add-user-form))
  (POST "/register" [name password] (db/add-user name password)
	                            (redirect "/"))
  (form-authentication-routes (fn [_ c] (html/layout "Login" c)) (form-authentication-adapter))	     
  (route/files "/")
  (route/not-found "<h1>Not Found</h1>"))

(def ddroutes (-> droutes
		  (with-security security-policy form-authentication)
                   wrap-stateful-session))

(defn start []
  (run-jetty (var ddroutes) {:port 8080
                           :join? false}))