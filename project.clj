(defproject raster "0.1.0-SNAPSHOT"
  :description "A library for working with ImageData in ClojureScript."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;[org.clojure/clojurescript "0.0-1586"]
                 [com.cemerick/clojurescript.test "0.0.3"]]
  :plugins  [[lein-cljsbuild "0.3.0"]]
  :cljsbuild {:builds
              [{:source-paths ["src"]
                :id "dev"
                :compiler {:pretty-print true
                           :output-to "resources/public/js/main.js"
                           :optimizations :whitespace}}
               {:source-paths ["src"]
                :id "prod"
                :compiler {:pretty-print false
                           :output-to "resources/public/js/main.js"
                           :optimizations :advanced}}]
              :repl-listen-port 9200}
  )
