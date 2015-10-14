(ns ^:figwheel-always try-om-next.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def app-state 
  (atom 
    {:app/title "Feeding time at the zoo"
     :animals/list (into []
                         (map-indexed 
                           (fn [i v] [i v]) 
                           ["Ant" "Bear" "Crocodile" "Dodo" "Elephant" "Fish" "Gorilla" "Hamster" "Iguana" "Jackal"]))}))

(defmulti read (fn [_ key _] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defmethod read :animals/list
  [{:keys [state]} key {:keys [start end]}]
  {:value (subvec (:animals/list @state) start end)})

(defui AnimalList
  static om/IQueryParams
  (params
    [this]
    {:start 0 :end 10})

  static om/IQuery
  (query
    [this]
    '[:app/title (:animals/list {:start ?start :end ?end})])

  Object
  (render
    [this]
    (let [{:keys [app/title animals/list]} (om/props this)]
      (dom/div
        nil
        (dom/title nil title)
        (apply dom/ul nil
               (map (fn [[i name]]
                      (dom/li nil (str i ". " name)))
                    list))))))

(def reconciler
  (om/reconciler
    {:state app-state
     :parser (om/parser {:read read})}))

(om/add-root! 
  reconciler
  AnimalList
  (js/document.getElementById "app"))

(println (om/get-query (om/class->any reconciler AnimalList)))

#_(om/set-params!
    (om/class->any reconciler AnimalList)
    {:start 0 :end 5})

#_(reset! app-state 
          (om/from-history 
            reconciler 
            (-> reconciler :config :history (.-arr) last))) ; this is NOT safe!

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

