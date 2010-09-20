(ns diverging-diamond.db-layer
  (:use [clojure.contrib.sql :as sql]
	[sandbar.auth :only [current-username]])
  (:import jBCrypt.BCrypt))

(def db {:classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "didi.db"})

(defn initdb []
  "Initialize the tables"
  (sql/with-connection db
    (sql/create-table
     :links
     [:id "INTEGER" "PRIMARY KEY"]
     [:score :int "DEFAULT 0"]
     [:title :text "NOT NULL"]
     [:url :text "NOT NULL"]
     [:added_by :text]))

  (sql/with-connection db
    (sql/create-table
     :users
     [:id "INTEGER" "PRIMARY KEY"]
     [:username :text "NOT NULL"]
     [:password :text "NOT NULL"]
     [:role :text "NOT NULL DEFAULT 'user'"])))

(defn add-link-to-db [title url]
  "add a link to the db"
  (let [url (if (re-matches #"http://.*" url)
	      url
	      (str "http://" url))]
    (sql/with-connection db
      (seq
       (sql/insert-values :links
         [:title :url :added_by]
	 [title url (current-username)])))))

(defn get-links []
  "get a seq of all links in the db"
  ;;FIXME: Add limitation to selection
  (sql/with-connection db
    (sql/with-query-results res
      ["select * from links"]
      (doall res))))

;;TODO: write change-score function to update the score when voting.

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


(comment
  ;;useless, i think?
  (defn get-hash [name]
    (sql/with-connection db
      (sql/with-query-results res
	["select password from users where username=?" name]
	(:password (first (doall res))))))

  (defn validate [name password]
    (if-let [psw (get-hash name)]
      (BCrypt/checkpw password psw))))