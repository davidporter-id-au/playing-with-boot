(def project 'myapp)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [org.clojure/core.async "0.4.474"]
                            [clj-http "3.7.0"]
                            [cheshire "5.8.0"]
                            [org.clojure/tools.reader "1.2.1"]])

(task-options!
 aot {:namespace   #{'myapp.core}}
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/myapp"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 jar {:main        'myapp.core
      :file        (str "myapp-" version "-standalone.jar")})

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (aot) (pom) (uber) (jar) (target :dir dir))))

(deftask run
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (require '[myapp.core :as app])
  (apply (resolve 'app/-main) args))

(require '[adzerk.boot-test :refer [test]])
