(ns diverging-diamond.db-layer
  (:use [clojure.contrib.sql :as sql])
  (:import jBCrypt.BCrypt))

(def db {:classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "didi.db"})

(defn initdb []
  (sql/with-connection db
   (sql/create-table
      :links0
      [:id "INTEGER" "PRIMARY KEY"]
      [:title :text "NOT NULL"]
      [:url :text "NOT NULL"]
      [:created_at :datetime])))

(defn create-users []
  (sql/with-connection db
    (sql/create-table
     :users
     [:id "INTEGER" "PRIMARY KEY"]
     [:username :text "NOT NULL"]
     [:password :text "NOT NULL"])))

(defn now []
  "create an sql timestamp"
  (java.sql.Timestamp. (.getTime (java.util.Date.))))

(defn add-link-to-db [title url]
  "add a link to the db"
  (let [timestamp (now)
	url (if (re-matches #"http://.*" url)
	      url
	      (str "http://" url))]
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

;;;;;;;user management;;;;;;;;;;;
(defn add-user [name password]
  (let [psw (BCrypt/hashpw password (BCrypt/gensalt))]
    (sql/with-connection db
      (seq
       (sql/insert-values :users
         [:username :password]
	 [name psw])))))

(defn find-user [name]
  (sql/with-connection db
    (sql/with-query-results res
      ["select username,password from users where username=?" name]
      (first (doall res)))))

(defn get-hash [name]
  (sql/with-connection db
   (sql/with-query-results res
     ["select password from users where username=?" name]
     (:password (first (doall res))))))

(defn validate [name password]
  (if-let [psw (get-hash name)]
    (BCrypt/checkpw password psw)))