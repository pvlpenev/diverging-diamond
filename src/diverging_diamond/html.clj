(ns diverging-diamond.html
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers]
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
	[:span (link-to "/logout" (str "Log out ("(current-username)")"))]
	[:span (link-to "/login"  "Log-in")])

      (when-not (any-role-granted? :admin :user)
	[:span [:a {:href "/register" :class "add"} "Register user"]])]]
    
    [:div {:id "content"}
     [:div {:id "greeting"} body]]]))

(defn home []
  (let [links (db/get-links)]
    (layout "Home"
	    (for [link links]
	       [:div (:id link) ". "  [:a {:href (:url link)}
				       (:title link)]]))))
