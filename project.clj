(defproject try-om-next "0.1.0-SNAPSHOT"
  :description "Working through the om next wiki example "
  :url "https://github.com/christoph-frick/om-next-wiki-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.omcljs/om "1.0.0-alpha1"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]

                        :figwheel { :on-jsload "try-om-next.core/on-js-reload" }

                        :compiler {:main try-om-next.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/try_om_next.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/try_om_next.js"
                                   :main try-om-next.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             ; from here on, all is for fireplace.  drop if you follow the wiki
             :nrepl-port 7888
             :nrepl-middleware ["cider.nrepl/cider-middleware"
                                "cemerick.piggieback/wrap-cljs-repl"]
             }
  :profiles {:dev {:dependencies  [[com.cemerick/piggieback "0.2.1"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  )
