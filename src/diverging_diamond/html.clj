(ns diverging-diamond.html
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers]
	[hiccup.form-helpers]
	[sandbar.auth :only [any-role-granted? current-username]])
  (:require [diverging-diamond.db-layer :as db]))

(defn layout [title body]
  (html
   (doctype :html5)
   [:head
    [:title title]
    (include-css "/stylesheets/screen.css")]
   [:body
    [:div {:id "header"}
     [:h1 "Basic Compojure Application with Styles"
      [:span [:a {:href "/" :id "home"} "Home"]]
      [:span [:a {:href "/add" :class "add"} "Add link"]]

      (if (any-role-granted? :admin :user)
	[:span (link-to  "/logout" (str "Log out (" (current-username)")"))]
	[:span (link-to "/login"  "Log-in")])

      (when-not (any-role-granted? :admin :user)
	[:span [:a {:href "/register" :class "add"} "Register user"]])]]
    
    [:div {:id "content"}
     [:div {:id "greeting"} body]]]))

;;TODO: rewrite home with new features

(defn home []
  (let [links (db/get-links)]
    (layout "Home"
	    (for [link links]
	       [:div (:id link) ". "  [:a {:href (:url link)} (:title link)]]))))

;;FIXME: add form validation

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
