(ns data.nmea-0183.message-test
  (:require [data.nmea-0183.message :as msg]
            [data.nmea-0183.input :as input]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.java.io :as io]))


;; Test sample file from https://en.wikipedia.org/wiki/NMEA_0183
(deftest sample-file-messages
  (with-open [in (io/input-stream "file:test/resources/test-messages.txt")]
    (let [in-fn (input/input-stream in)
          msgs (repeatedly 14 #(msg/read-message in-fn))
          by-sentence (group-by :sentence msgs)]

      (println (first msgs))
      (is (every? #(= "GP" (:talker %)) msgs))

      (is (= 2
             (count (by-sentence "GGA"))
             (count (by-sentence "GSA"))
             (count (by-sentence "RMC"))
             (count (by-sentence "RRE"))))
      (is (= 6 (count (by-sentence "GSV")))))))

(defn- msg [string]
  (msg/read-message
   (input/input-stream
    (java.io.ByteArrayInputStream.
     (.getBytes (str string \return \newline) "US-ASCII")))))

(deftest test-invalid-checksum-throws
  (is (thrown-with-msg? Exception #"Checksum mismatch"
               (msg "$GPGGA,092750.000,5321.6802,N,00630.3372,W,1,8,1.03,61.7,M,55.2,M,,*ff")))
  (is (thrown-with-msg? Exception #"Checksum mismatch"
                        (msg "$GPRRE,10,01,-000.105,06,-000.030,10,+000.034,12,+000.166,15,-000.193,17,+000.059,19,-000.014,24,-000.118,25,+000.479,32,-000.126,0000.240,0000.334*73"))))
