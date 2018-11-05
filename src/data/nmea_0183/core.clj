(ns data.nmea-0183.core
  "NMEA 0183 parser and writer."
  (:require [data.nmea-0183.message :as msg]
            [data.nmea-0183.sentences :as sentences]))

(defn parse
  "Read and parse one sentence from the input source."
  [input]
  (let [{:keys [sentence fields] :as m} (msg/read-message input)]
    (merge m (sentences/parse-sentence sentence fields))))
