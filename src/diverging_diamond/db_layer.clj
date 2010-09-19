(ns diverging-diamond.db-layer
  (:use [clojure.contrib.sql :as sql]))

(def db {:classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "didi.db"})

(defn initdb []
  (sql/with-connection db
   (sql/create-table
      :links
      [:id "INTEGER" "PRIMARY KEY"]
      [:title :text "NOT NULL"]
      [:url :text "NOT NULL"]
      [:created_at :datetime])))

(defn now []
  "create an sql timestamp"
  (java.sql.Timestamp. (.getTime (java.util.Date.))))

(defn add-link-to-db [title url]
  "add a link to the db"
  (let [timestamp (now)]
    (sql/with-connection db
      (seq
       (sql/insert-values :links
         [:title :url :created_at]
	 [title url timestamp])))))

(defn get-links []
  "get a seq of all links in the db"
  (sql/with-connection db
    (sql/with-query-results res
      ["select * from links"]
      (doall res))))
