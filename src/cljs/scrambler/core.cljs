(ns scrambler.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [scrambler.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary :include-macros true])
  (:import goog.History))

(defonce session (r/atom {:page :home
                          :src ""
                          :target ""
                          :result nil}))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page (:page @session)) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "scrambler"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]]]])

(defn str-input [param value]
  [:input.form-control {:type "text"
                        :value value
                        :on-change
                        #(swap! session assoc
                               param (.. % -target -value)
                               :result nil
                               )}])

(defn fetch-results! []
  (POST "/scramble" {:handler #(swap! session merge %)
                     :response-format :transit
                     :params (select-keys @session [:src :target])}))

(defn scrambler-form []
  (let [{:keys [src target result] :as data} @session]
   [:form#scrambler-form
    [:div.form-group
     [:label {:for "src"} "Can this text: "]
     [str-input :src src]]
    [:div.form-group
     [:label {:for "target"} "be made from this:"]
     [str-input :target target]]
    [:button.btn-primary
     {:type "button"
      :on-click #(do (swap! session assoc :result nil)
                  (fetch-results!))}
     "Find out!"]]))

(defn home-page []
  [:div.container
   (let [{:keys [src target result]} @session]
     (if (some? result)
       (if result
         [:div.alert.alert-success
          (str "Yes! \"" target "\" can be made from \"" src "\"")]
         [:div.alert.alert-danger
          "Sorry! Nope! \"" target "\" cannot be made from \"" src "\"" ])
       [:div.alert.alert-warning "Click to find out!"]))
   [:div.row>div.col-sm-12
    [scrambler-form]]])

(def pages
  {:home #'home-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! session assoc :page :home))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
            (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))
