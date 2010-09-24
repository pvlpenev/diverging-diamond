(ns diverging-diamond.forms
  (:use [sandbar.auth :only [defauth]]
        [sandbar.validation :only [add-validation-error]])
  (:require [diverging-diamond.db-layer :as db]
	    [sandbar.forms :as forms])
  (:import jBCrypt.BCrypt))

(forms/defform add-link-form "/add"
  :fields [(forms/textfield "Title" :title)
	   (forms/textfield "URL"   :url)]
   :on-cancel "/"
   :on-success #(do (db/add-link-to-db (:title %)
				       (:url   %))
		    "/"))

(forms/defform add-user-form "/register"
  :fields [(forms/textfield "Username" :name)
	   (forms/password "Password" :password)]
  :on-cancel "/"
  :on-success #(do (db/add-user (:name %)
				(:password %))
		   "/login"))

(defauth auth-user-form
  :type :form
  :load (fn [username password]
	  (merge (db/find-user username)
		 {:login-password password :roles #{:user}}))
  :validator (fn [m]
	       (if (BCrypt/checkpw (:login-password m)
				   (:password m))
		 m
		 (add-validation-error m :password
				       "Unable to authenticate user.")))
  :properties {:username "Username:"
               :password "Password:"
               :username-validation-error "Please enter a username!"
               :password-validation-error "Please enter a password!"
	       :login-page "/"
	       :logout-page "/"})