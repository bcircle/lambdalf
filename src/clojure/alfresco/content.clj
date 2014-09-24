;
; Copyright (C) 2011,2012 Carlo Sciolla
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
 
(ns alfresco.content
  (:require [alfresco.core :as c])
  (:import [org.alfresco.model ContentModel]
           [java.io File ByteArrayInputStream]))

(defn content-service
  []
  (.getContentService (c/alfresco-services)))

(defn- is
  "Retrieves an InputStream of the content for the provided node"
  [node]
  (.getContentInputStream (.getReader (content-service) node ContentModel/PROP_CONTENT)))

(defn get-reader
  "Returns the ContentReader for the given node & property (defaults to cm:content).
  Should not normally be used directly - read! is preferable."
  ([node]          (get-reader node ContentModel/PROP_CONTENT))
  ([node property] (.getReader (content-service) node property)))

(defn size
  "Returns the size (in bytes) of the given content property (defaults to cm:content)
  of the given node.  Returns nil if the node doesn't have that property."
  ([node] (size node ContentModel/PROP_CONTENT))
  ([node property]
    (let [reader (get-reader node property)]
      (if (not (nil? reader))
        (.getSize reader)
        nil))))

(defn encoding
  "Returns the encoding of the given content property (defaults to cm:content)
  of the given node.  Returns nil if the node doesn't have that property."
  ([node] (encoding node ContentModel/PROP_CONTENT))
  ([node property]
    (let [reader (get-reader node property)]
      (if (not (nil? reader))
        (.getEncoding reader)
        nil))))

(defn locale
  "Returns the java.util.Locale of the given content property (defaults to cm:content)
  of the given node.  Returns nil if the node doesn't have that property."
  ([node] (locale node ContentModel/PROP_CONTENT))
  ([node property]
    (let [reader (get-reader node property)]
      (if (not (nil? reader))
        (.getLocale reader)
        nil))))

(defn mime-type
  "Returns the MIME type of the given content property (defaults to cm:content)
  of the given node.  Returns nil if the node doesn't have that property."
  ([node] (mime-type node ContentModel/PROP_CONTENT))
  ([node property]
    (let [reader (get-reader node property)]
      (if (not (nil? reader))
        (.getMimetype reader)
        nil))))

;; as seen on
;; https://groups.google.com/group/clojure/browse_thread/thread/e5fb47befe8b9199
;; TODO: make sure we're not breaking utf-8 support
;; TODO: consider alternative forms that return content as streams, strings, etc.
(defn read!
  "Returns a lazy seq of the content of the provided node"
  [node]
  (let [is (is node)]
    (map char (take-while #(not= -1 %) (repeatedly #(.read is))))))

(defn get-writer
  "Returns the ContentWriter for the given node & property (default to cm:content).
   Should not normally be used directly - write! is preferable."
  ([node]          (get-writer node ContentModel/PROP_CONTENT))
  ([node property] (.getWriter (content-service) node property true)))

(defmulti write!
  "Writes content to the given node."
  #(type (first %&)))

(defmethod write! java.io.InputStream
  ([src node]          (.putContent (get-writer node) src))
  ([src node property] (.putContent (get-writer node property) src)))

(defmethod write! java.lang.String
  ([src node]          (write! (ByteArrayInputStream. (.getBytes src "UTF-8")) node))
  ([src node property] (write! (ByteArrayInputStream. (.getBytes src "UTF-8")) node property)))

(defmethod write! java.io.File
  ([src node]          (.putContent (get-writer node) src))
  ([src node property] (.putContent (get-writer node property) src)))
