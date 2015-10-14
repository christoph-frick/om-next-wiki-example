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

(defn mutate
  [{:keys [state] :as env} key params]
  (do ; additional debug
    (println "Mutate called with:")
    (println @state)
    (println key)
    (println params))
  (if (= 'increment key)
    {:value [:counter]
     :action #(swap! state update-in [:counter] inc)}
    {:value :not-found}))

(def my-parser (om/parser {:read read :mutate mutate}))

(def my-state (atom {:counter 0}))

(println (my-parser {:state my-state} [:counter :title]))

(my-parser {:state my-state} '[(increment)])

(println @my-state)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

