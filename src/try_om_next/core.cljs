(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(defui HelloWorld
  Object
  (render [this]
          (dom/div nil "Hello World!")))

(def app (om/factory HelloWorld))

(js/React.render (app) (js/document.getElementById "app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

