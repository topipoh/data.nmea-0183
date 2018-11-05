(ns data.nmea-0183.core-test
  (:require [data.nmea-0183.core :as sut]
            [clojure.test :as t :refer [deftest is testing]]
            [data.nmea-0183.input :as input]
            [data.nmea-0183.fields :as f]
            [data.nmea-0183.sentences :as sentences]))

(defn- input [msg-str]
  (input/input-stream (java.io.ByteArrayInputStream.
                       (.getBytes (str msg-str \return \newline) "US-ASCII"))))


(deftest parse-gsa-test
  (let [m (sut/parse (input "$GPGSA,A,3,10,07,05,02,29,04,08,13,,,,,1.72,1.03,1.38*0A"))
        {::f/keys [faa-mode fix-status satellite-ids position-dop horizontal-dop vertical-dop] :as gsa}
        (::sentences/gsa m)]
    (println (pr-str gsa))
    (is (= :automatic faa-mode))
    (is (= :3d fix-status))
    (is (= ["10" "07" "05" "02" "29" "04" "08" "13"] satellite-ids))
    (is (= 1.72 position-dop))
    (is (= 1.03 horizontal-dop))
    (is (= 1.38 vertical-dop))))
