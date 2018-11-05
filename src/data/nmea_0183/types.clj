(ns data.nmea-0183.types
  "Defines NMEA 0183 date types and their parsing from ASCII."
  (:require [clojure.spec.alpha :as s]))

;; Define data types
(s/def ::double double?)
(s/def ::string string?)

(s/def ::faa-mode #{:automatic :manual :dgps :estimated :precise :simulated :none})
(s/def ::gps-fix-status #{:na :2d :3d})
(s/def ::gps-fix-quality #{:invalid :normal :dgps :pps :rtk :frtk :estimated :manual :simulated})

;; Define a multi method to parse different field types from ascii

(defmulti from-ascii
  "Parse NMEA 0183 field from string to Clojure data. Dispatches on data type keyword."
  (fn [type ascii-value] type))

(defmethod from-ascii ::double [_ d]
  (Double/parseDouble d))

(defmethod from-ascii ::string [_ s] s)

(defmethod from-ascii ::faa-mode [_ mode]
  (case mode
    "A" :automatic
    "M" :manual
    "D" :dgps
    "E" :estimated
    "P" :precise
    "S" :simulated
    "N" :none))

(defmethod from-ascii ::gps-fix-status [_ status]
  (case status
    "1" :na
    "2" :2d
    "3" :3d))

(defmethod from-ascii ::gps-fix-quality [_ quality]
  (case quality
    "0" :invalid
    "1" :normal
    "2" :dgps
    "3" :pps
    "4" :rtk
    "5" :frtk
    "6" :estimated
    "7" :manual
    "8" :simulated))


;; Define named fields

(s/def ::position-dop ::double)
(s/def ::vertical-dop ::double)

(defn parse-field [field-kw ascii-value]
  (from-ascii (s/get-spec field-kw) ascii-value))
