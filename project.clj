;
; Copyright © 2011-2014 Carlo Sciolla
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
; Contributors:
;    Carlo Sciolla - initial implementation
;    Peter Monks   - contributor

; Make sure these line up to those provided in the specified Alfresco version or weird things can happen...
(def alfresco-version    "5.0.b")
(def spring-version      "3.0.5.RELEASE")
(def spring-surf-version "5.0.b")

(defproject org.clojars.lambdalf/lambdalf "0.2.0-SNAPSHOT"
  :title            "lambdalf"
  :description      "Lambdalf -- Clojure support for Alfresco"
  :url              "https://github.com/lambdalf/lambdalf"
  :license          { :name "Apache License, Version 2.0"
                      :url "http://www.apache.org/licenses/LICENSE-2.0" }
  :min-lein-version "2.4.0"
  :repositories [
                  ["alfresco.public" "https://artifacts.alfresco.com/nexus/content/groups/public/"]
                ]
  :dependencies [
                  ; Dependencies that will be included in the AMP - other dependencies should go in the appropriate profile below
                  [org.clojure/clojure     "1.6.0"]
                  [org.clojure/tools.nrepl "0.2.6"]
                ]
  :profiles {:dev      { :plugins [[lein-amp "0.6.0"]] }
             :test     { :dependencies [
                                         [clj-http                       "1.0.1"]
                                         [tk.skuro.alfresco/h2-support   "1.6"]
                                         [com.h2database/h2              "1.4.182"]
                                         [org.eclipse.jetty/jetty-runner "9.3.0.M1" :exclusions [org.eclipse.jetty/jetty-jsp]]
                                       ] }
             :provided { :dependencies [
                                         [org.alfresco/alfresco-core                            ~alfresco-version]
                                         [org.alfresco/alfresco-data-model                      ~alfresco-version]
                                         [org.alfresco/alfresco-mbeans                          ~alfresco-version]
                                         [org.alfresco/alfresco-remote-api                      ~alfresco-version]
                                         [org.alfresco/alfresco-repository                      ~alfresco-version]
                                         [org.springframework/spring-context                    ~spring-version]
                                         [org.springframework/spring-beans                      ~spring-version]
                                         [org.springframework.extensions.surf/spring-webscripts ~spring-surf-version]
                                       ] }
            }
  :aot               [alfresco]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :resource-paths    ["src/resource"]
  :amp-source-path   "src/amp"
  :amp-target-war    [org.alfresco/alfresco ~alfresco-version :extension "war"]
  :javac-target      "1.7"
  )
