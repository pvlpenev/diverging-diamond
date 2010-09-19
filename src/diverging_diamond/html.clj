(ns diverging-diamond.html
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype include-css]]))

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

