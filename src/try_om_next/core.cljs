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

(defmethod mutate 'point/dec
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
    (comment 'TODO)))

(defui RootView
  static om/IQuery
  (query
    [this]
    (let [subquery (om/get-query Person)]
      `[{:list/one ~subquery} {:list/two ~subquery}])))

(def norm-data 
  (om/normalize RootView init-data true))

#_(-> Person om.next/get-query meta)

(def parser
  (om/parser {:read read :mutate mutate}))

(def app-state
  (atom norm-data))

#_(parser {:state app-state} '[:list/one])
#_(parser {:state app-state} '[(points/inc {:name "A"})])
#_(parser {:state app-state} '[:list/one])
#_(parser {:state app-state} '[:list/two])

#_(def reconciler
  (om/reconciler {:state init-data
                  :parser (om/parser {:read read :mutate mutate})}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

