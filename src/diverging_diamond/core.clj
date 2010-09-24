(ns diverging-diamond.core
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core]
	[sandbar.auth :only [with-security]]
        [sandbar.stateful-session :only [wrap-stateful-session]]
	[diverging-diamond.html :only [layout]])
  (:require [compojure.route :as route]
	    [diverging-diamond.forms :as forms]))

(def security-policy
  [#".*\.(css|js|png|jpg|gif)$" :any
   #"/login.*" :any
   #"/logout.*" :any
   #"/permission-denied.*" :any
   #"/register" :any
   #"/add" #{:user :admin}
   #"/" :any])

(defn form-helper [title]
  (fn [_ content]
    (layout title content)))

(defroutes droutes
  (GET "/" [] (html/home))
  (forms/add-link-form (form-helper "Add a link"))
  (forms/add-user-form (form-helper "Register user"))
  (forms/auth-user-form (form-helper "Autenticate user"))     
  (route/files "/")
  (route/not-found "<h1>Not Found</h1>"))

(def ddroutes (-> droutes
		  (with-security security-policy forms/form-auth)
                   wrap-stateful-session))

(defn start []
  (run-jetty (var ddroutes) {:port 8080
                           :join? false}))