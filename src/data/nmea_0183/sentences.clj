(ns data.nmea-0183.sentences
  "Define NMEA 0183 sentences"
  (:require [clojure.spec.alpha :as s]
            [data.nmea-0183.fields :as f]
            [data.nmea-0183.types :as t]
            [clojure.string :as str]))

(s/def ::gsa (s/keys :req [::f/faa-mode ::f/fix-status ::f/satellite-ids
                           ::f/position-dop ::f/horizontal-dop ::f/vertical-dop]))

(defn- parse [field-values & field-keywords]
  (loop [result {}
         [value & values] field-values
         [kw & keywords] field-keywords]
    (if-not kw
      result
      (if (vector? kw)
        ;; Vector definition of [count field-type-keyword collection-keyword validator] that specifies a collection
        (let [[cnt kw coll-kw validator] kw
              new-value (f/parse-field kw value)
              result (if (or (nil? validator)
                             (validator new-value))
                       (update result coll-kw (fnil conj []) new-value)
                       result)]
          (if (= 1 cnt)
            ;; Last one parsed
            (recur result values keywords)
            ;; Still more to parse
            (recur result values (cons [(dec cnt) kw coll-kw validator] keywords))))

        ;; Regular single values field
        (recur (assoc result kw (f/parse-field kw value))
               values
               keywords)))))

(defmulti parse-sentence
  "Parse sentence by sentence id keyword. Takes the sentence id and collection of field values."
  (fn [sentence-kw field-values] sentence-kw))

(defmethod parse-sentence "GSA"
  [_ values]
  {::gsa
   (parse values
          ::f/faa-mode
          ::f/fix-status
          [12 ::f/satellite-id ::f/satellite-ids (complement str/blank?)]
          ::f/position-dop
          ::f/horizontal-dop
          ::f/vertical-dop)})
