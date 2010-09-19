(ns diverging-diamond.html
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers]
	[hiccup.form-helpers])
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
      [:span [:a {:href "/add" :id "add"} "Add link"]]]]
    [:div {:id "content"}
     [:div {:id "greeting"} body]]]))

(defn home []
  (let [links (db/get-links)]
    (layout "Home"
	    (for [link links]
	       [:div (:id link) ". "  [:a {:href (:url link)} (:title link)]]))))

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
