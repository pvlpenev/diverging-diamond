(ns diverging-diamond.forms
  (:use [diverging-diamond.html :only [layout]]
	[hiccup.core :only [html]]
        [hiccup.page-helpers]
	[hiccup.form-helpers]
	[sandbar.form-authentication :only [FormAuthAdapter]]
        [sandbar.validation :only [add-validation-error]])
  (:require [diverging-diamond.db-layer :as db])
  (:import jBCrypt.BCrypt))

(defn add-form []
  (layout "Add a link"
   (form-to [:post "/add" ]
	    [:div {:class "form"}
	     "Title: "
	     (text-field "title")]
	    [:div {:class "form"}
	     "Url: "
	     (text-field "url")]
	    (submit-button "Save" ))))

(defn add-user-form []
  (layout "Register a new user"
   (form-to [:post "/register" ]
	    [:div {:class "form"}
	     "Username: "
	     (text-field "name")]
	    [:div {:class "form"}
	     "Password: "
	     (password-field "password")]
	    (submit-button "Save" ))))

(defrecord AuthAdapter []
  FormAuthAdapter
  
  (load-user [this username password]
	     ;;FIXME add admin roles
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