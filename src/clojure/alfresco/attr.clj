;
; Copyright (C) 2014 Peter Monks
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

(ns alfresco.attr
  (:refer-clojure :exclude [get set! list])
  (:require [clojure.string :as s]
            [alfresco.core  :as c]
            [alfresco.nodes :as n])
  (:import [java.io Serializable]
           [org.alfresco.service.cmr.attributes AttributeService
                                                AttributeService$AttributeQueryCallback]))

(defn ^AttributeService attribute-service
  "The attribute service bean."
  []
  (.getAttributeService (c/alfresco-services)))

(defn- construct-keys
  "Constructs keys in the format expected by the native attribute service."
  ([namespace]         (into-array Serializable [namespace]))
  ([namespace name]    (into-array Serializable [namespace name]))
  ([namespace name id] (into-array Serializable [namespace name id])))

(defn- deconstruct-keys
  "Deconstructs keys from the native attribute service into a vector of three elements (some of which may be nil)."
  [keys]
  (condp = (alength keys)
    1 [(aget keys 0) nil           nil]
    2 [(aget keys 0) (aget keys 1) nil]
    3 [(aget keys 0) (aget keys 1) (aget keys 2)]))

(defn- handle-attribute
  [result attr-id value keys]
  (let [[namespace name id] (deconstruct-keys keys)]
    (conj result { :namespace namespace
                   :name      name
                   :id        id
                   :value     value })))

(defn- list-impl
  "Private implementation of list-attrs."
  [keys]
  (let [result   (atom [])
        callback (proxy [AttributeService$AttributeQueryCallback] []
                   (handleAttribute [attr-id value keys]
                     (swap! result handle-attribute attr-id value keys)
                     true))
        _        (.getAttributes (attribute-service) callback keys)]
    @result))

(defn exists?
  "Does the given attribute exist?"
  [namespace name id]
  (.exists (attribute-service) (construct-keys namespace name id)))

(defn list
  "List all attributes in the given namespace, or namespace+name.  Result is a vector of maps, each map containing the keys:
  :namespace - the namespace of the attribute
  :name      - the name of the attribute
  :id        - the id
  :value     - the value for that id"
  ([namespace]      (list-impl (construct-keys namespace)))
  ([namespace name] (list-impl (construct-keys namespace name))))

(defn get
  "Returns the current value of the given attribute."
  [namespace name id]
  (.getAttribute (attribute-service) (construct-keys namespace name id)))

(defn set!
  "Unconditionally sets the value of the given attribute, creating it if it doesn't already exist."
  [namespace name id value]
  (.setAttribute (attribute-service) value (construct-keys namespace name id)))

(defn delete!
  "Deletes one or more attributes from the system."
  ([namespace]         (.removeAttributes (attribute-service) (construct-keys namespace)))
  ([namespace name]    (.removeAttributes (attribute-service) (construct-keys namespace name)))
  ([namespace name id] (.removeAttribute  (attribute-service) (construct-keys namespace name id))))
