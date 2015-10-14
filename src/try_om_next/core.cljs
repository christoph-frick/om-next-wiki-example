(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def app-state (atom {:counter 0}))

(defn read
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defn mutate
  [{:keys [state] :as env} key params]
  (if (= 'increment key)
    {:value [:counter]
     :action #(swap! state update-in [:counter] inc)}
    {:value :not-found}))

(defui Counter
  static om/IQuery
  (query
    [this]
    [:counter])

  Object
  (render
    [this]
    (let [{:keys [counter]} (om/props this)]
      (dom/div
        nil
        (dom/span nil (str "Count: " counter))
        (dom/button
          #js {:onClick (fn [e]
                          (om/transact! this '[(increment)]))}
          "increment")))))

(def reconciler
  (om/reconciler
    {:state app-state
     :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! 
  reconciler
  Counter
  (js/document.getElementById "app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

