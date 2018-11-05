(ns data.nmea-0183.fields
  "Defines NMEA 0183 fields."
  (:require [clojure.spec.alpha :as s]
            [data.nmea-0183.types :as t]))


(s/def ::position-dop ::t/double)
(s/def ::horizontal-dop ::t/double)
(s/def ::vertical-dop ::t/double)

(s/def ::faa-mode ::t/faa-mode)
(s/def ::fix-status ::t/gps-fix-status)

(s/def ::satellite-id ::t/string)
(s/def ::satellite-ids (s/coll-of ::satellite-id))

(defn parse-field [field-kw ascii-value]
  (t/from-ascii (s/get-spec field-kw) ascii-value))
