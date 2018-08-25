(ns scrambler.routes.home
  (:require [scrambler.layout :as layout]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :as response]
            [schema.core :as s]
            [clojure.java.io :as io]))

(s/defschema Scrambler
  {:src s/Str
   :target s/Str
   (s/optional-key :result) s/Bool})

(defn- has-all-chars?
  "returns true if a has all the chars of b, otherwise false"
  [a b]
  (let [[as bs] (mapv frequencies [a b])
        fas (select-keys as (keys bs))]
    (every? true?
            (vals
             (merge-with #(>= %1 %2) fas bs)))))

(defn scramble?
  "returns true if some characters in a can be rearranged to get str b"
  [a b]
  (cond
    (< (.length a) (.length b))    false
    (or (empty? a) (empty? b))     false
    :else (has-all-chars? a b)))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (POST "/scramble" []
        :body [sr Scrambler]
        :return Scrambler
        (-> (response/ok (assoc sr
                                :result (scramble? (:target sr) (:src sr))))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))
