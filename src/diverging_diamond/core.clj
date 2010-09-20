(ns diverging-diamond.core
  (:use [ring.adapter.jetty :only [run-jetty]]
	[ring.util.response :only [redirect]]
        [compojure.core]
	[sandbar.auth :only [with-security any-role-granted?]]
        [sandbar.form-authentication :only [form-authentication-routes form-authentication FormAuthAdapter]]
        [sandbar.stateful-session :only [wrap-stateful-session]]
        [sandbar.validation :only [add-validation-error]])
  (:require [compojure.route :as route]
	    [diverging-diamond.db-layer :as db]
	    [diverging-diamond.html :as html])
  (:import jBCrypt.BCrypt))

(defrecord AuthAdapter []
  FormAuthAdapter
  
  (load-user [this username password]
      (merge (db/find-user username)
	     {:login-password password :roles #{:user}}))
  
  (validate-password [this]
                     (fn [m]
                       (let [p (BCrypt/checkpw (:login-password m)
					    (:password m))]
			 (if p
			   m
			   (add-validation-error m :password "Unable to authenticate user."))))))
	     

(defn form-authentication-adapter []
  (merge
   (AuthAdapter.)
   {:username "Username"
    :password "Password"
    :username-validation-error "You must supply a valid username."
    :password-validation-error "You must supply a password."
    :login-page "/"
    :logout-page "/"}))

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
  (GET "/add" [] (html/add-form))
  (POST "/add" [title url] (db/add-link-to-db title url)
	                   (redirect "/"))
  (GET "/register" [] (html/add-user-form))
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