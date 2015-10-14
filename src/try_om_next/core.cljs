(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(defui HelloWorld
  Object
  (render [this]
          (dom/div nil (get (om/props this) :title))))

(def app (om/factory HelloWorld))

(js/React.render 
  (apply dom/p nil
         (map #(app {:title (str "Hello " %)})
              (range 3)))
  (js/document.getElementById "app"))

;;; fails to reload (e.g. change the number in the range)
; Uncaught Error: Invariant Violation: processUpdates(): Unable to find child
; 0 of element. This probably means the DOM was unexpectedly mutated (e.g., by
; the browser), usually due to forgetting a <tbody> when using tables, nesting
; tags like <form>, <p>, or <a>, or using non-SVG elements in an <svg> parent.
; Try inspecting the child nodes of the element with React ID `.0`.

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

