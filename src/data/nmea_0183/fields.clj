(ns data.nmea-0183.fields
  "Defines NMEA 0183 fields."
  (:require [clojure.spec.alpha :as s]
            [data.nmea-0183.types :as t]
            [data.nmea-0183.fields :as f]
            [clojure.string :as str]))


(s/def ::position-dop ::t/double)
(s/def ::horizontal-dop ::t/double)
(s/def ::vertical-dop ::t/double)

(s/def ::faa-mode ::t/faa-mode)
(s/def ::fix-status ::t/gps-fix-status)

(s/def ::satellite-id ::t/string)
(s/def ::satellite-ids (s/coll-of ::satellite-id))

(s/def ::heading ::t/double)
(s/def ::true-indicator ::t/boolean)

(s/def ::true-course ::t/double)
(s/def ::magnetic-course ::t/double)
(s/def ::magnetic-indicator ::t/string) ;; FIXME: always just "M"?

(s/def ::speed-knots ::t/double)
(s/def ::knots-indicator ::t/string) ;; FIXME: always just "N"

(s/def ::speed-kmph ::t/double)
(s/def ::kmph-indicator ::t/string) ;; FIXME: always just "K"

(s/def ::time ::t/utc-time)
(s/def ::day ::t/integer)  ;; FIXME: stricted spec for date fields
(s/def ::month ::t/integer)
(s/def ::year ::t/integer)
(s/def ::local-zone-hours ::t/integer)
(s/def ::local-zone-minutes ::t/integer)

(s/def ::latitude ::t/latitude)
(s/def ::longitude ::t/longitude)
(s/def ::lat-hemisphere ::t/hemisphere)
(s/def ::lon-hemisphere ::t/hemisphere)
(s/def ::fix-quality ::t/gps-fix-quality)
(s/def ::satellites-in-use ::t/integer)
(s/def ::horizontal-dilution ::t/double)
(s/def ::altitude ::t/double)
(s/def ::altitude-units ::t/units)
(s/def ::geoidal-height ::t/double)
(s/def ::height-units ::t/units)
(s/def ::dgps-age ::t/double)
(s/def ::dgps-station-id ::t/string)

(s/def ::rate-of-turn ::t/double)
(s/def ::data-status ::t/data-status)

(s/def ::range-residual ::t/double)
(s/def ::range-residuals (s/map-of ::satellite-id ::range-residual))
(s/def ::hpos-err-estimate ::t/double)
(s/def ::vpos-err-estimate ::t/double)

(s/def ::course ::t/double)

(s/def ::date ::t/date)
(s/def ::magnetic-variation ::t/double)
(s/def ::variation-hemisphere ::t/hemisphere)

(defn parse-field [field-kw ascii-value]
  (let [spec (s/get-spec field-kw)]
    (when-not spec
      (throw (ex-info "Missing spec for field, this is a bug."
                      {:field-kw field-kw})))
    (when-not (str/blank? ascii-value)
      (t/from-ascii spec ascii-value))))
