(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def init-data
  {:list/one [{:name "A" :points 0}  
              {:name "B" :points 0}  
              {:name "C" :points 0}]
   :list/two [{:name "A" :points 0 :age 27}  
              {:name "D" :points 0}  
              {:name "E" :points 0}]})

(defmulti read om/dispatch)

(defn get-people
  [state key]
  (let [st @state]
    (into [] (map #(get-in st %)) (get st key))))

(defmethod read :list/one
  [{:keys [state] :as env} key params]
  {:value (get-people state key)})

(defmethod read :list/two
  [{:keys [state] :as env} key params]
  {:value (get-people state key)})

(defmulti mutate om/dispatch)

(defmethod mutate 'points/inc
  [{:keys [state]} _ {:keys [name]}]
  {:action (fn []
             (swap! state update-in
                    [:person/by-name name :points]
                    inc))})

(defmethod mutate 'points/dec
  [{:keys [state]} _ {:keys [name]}]
  {:action (fn []
             (swap! state update-in
                    [:person/by-name name :points]
                    #(let [n (dec %)] (if (neg? n) 0 n))))})

(defui Person
  static om/Ident
  (ident
    [this {:keys [name]}]
    [:person/by-name name])

  static om/IQuery
  (query 
    [this]
    '[:name :points :age])

  Object
  (render
    [this]
    (let [{:keys [points name foo] :as props} (om/props this)]
      (dom/li 
        nil
        (dom/label
          nil
          (str name ", points: " points))
        (dom/button
          #js {:onClick
               (fn [e]
                 (om/transact! this `[(points/inc ~props)]))}
          "+")
        (dom/button
          #js {:onClick
               (fn [e]
                 (om/transact! this `[(points/dec ~props)]))}
          "-")))))

(def person (om/factory Person {:keyfn :name}))

(defui ListView
  Object
  (render
    [this]
    (let [list (om/props this)]
      (apply dom/ul nil
             (map person list)))))

(def list-view (om/factory ListView))

(defui RootView
  static om/IQuery
  (query
    [this]
    (let [subquery (om/get-query Person)]
      `[{:list/one ~subquery} {:list/two ~subquery}]))

  Object
  (render
    [this]
    (let [{:keys [list/one list/two]} (om/props this)]
      (apply dom/div nil
             [(dom/h2 nil "List One")
              (list-view one)
              (dom/h2 nil "List Two")
              (list-view two)]))))

(def norm-data 
  (om/normalize RootView init-data true))

(def reconciler
  (om/reconciler {:state init-data
                  :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler RootView (js/document.getElementById "app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

