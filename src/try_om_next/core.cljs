(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(defn read
  [{:keys [state] :as env} key params]
  (do ; additional debug
    (println "Read called with:")
    (println @state)
    (println key)
    (println params))
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(def my-parser (om/parser {:read read}))

(def my-state (atom {:counter 0}))

(println (my-parser {:state my-state} [:counter :title]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

