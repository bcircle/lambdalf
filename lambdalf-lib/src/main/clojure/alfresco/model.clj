(ns alfresco.model
  (:require [alfresco.core :as c])
  (:import [org.alfresco.service.namespace QName]))

(defn namespace-service
  []
  (.getNamespaceService (c/alfresco-services)))

(defn dictionary-service
  []
  (.getDictionaryService (c/alfresco-services)))

;; TODO: there are better ways, for sure
(defn- qname-dispatch
  [from]
  (cond
   (= QName (class from)) ::identity
   (= String (class from)) (if (= "{" (first from))
                             ::create ;; (hopefully) like {http://my.model}content
                             ::str-shortname) ;; something like cm:content
   (keyword? from) ::from-keyword
   :else ::create)) ;; this will most likely always fail

(defmulti qname
  "Transforms the input parameter in a QName"
  qname-dispatch
  :default ::create)

(defmethod qname ::identity [^QName q]
           q)

(defmethod qname ::create [qname-expr]
           (if qname-expr (QName/createQName qname-expr)))

(defmethod qname ::str-shortname [^String qname-str]
           (let [[prefix name] (QName/splitPrefixedQName qname-str)]
             (QName/createQName prefix name (namespace-service))))

(defmethod qname ::from-keyword [k]
           (qname (name k)))

(defn qname-str
  "Translates a QName into a human readable prefixed string"
  [^QName q]
  (.toPrefixString q (namespace-service)))

(defn in?
  "True if the qname is within ns. ns can be either short or long format."
  [ns ^QName qname]
  (let [qname-ns (.getNamespaceURI qname)]
    (or
     (= qname-ns ns)
     (= qname-ns (.getNamespaceURI (namespace-service) ns)))))

(defn qname-isa?
  "True if child is in the inheritance tree of parent"
  [child parent]
  (.isSubClass (dictionary-service) (qname child) (qname parent)))
