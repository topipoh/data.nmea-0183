(ns data.nmea-0183.message
  "NMEA 0183 parser and writer: message format.
  Reads the raw ASCII based message format, checks checksum and returns message parts.
  Based on reverse engineered specification at https://en.wikipedia.org/wiki/NMEA_0183")

(def control-characters
  {\return :cr    ; Carriage return
   \newline :lf   ; Line feed, end delimiter
   \! :start-enc  ; '!' start of encapsulation sentence delimiter
   \$ :start      ; '$' Start delimiter
   \* :checksum   ; '*' Checksum delimiter
   \, :field      ; ',' Field delimiter
   \\ :tag-block  ; '\' TAG block delimiter
   \^ :code       ; '^' Code delimiter for HEX representation of ISO/IEC 8859-1 (ASCII) characters
   \~ :reserved}) ; '~' Reserved

(def hex-digit-characters #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \a \b \c \d \e \f})

(def start-characters #{:start :start-enc})

(defn start-character? [ch]
  (start-characters (control-characters ch)))

(defn read-hex-number [in-fn]
  (loop [digits []
         ^char ch (in-fn)]
    (if-let [digit (hex-digit-characters (Character/toLowerCase ch))]
      (recur (conj digits digit) (in-fn))
      (do
        ;; unread last char
        (in-fn ch)
        (Integer/parseInt (apply str digits) 16)))))

(defn read-end [in-fn]
  (let [cr (in-fn)
        lf (in-fn)]
    (when-not (and (= (control-characters cr) :cr)
                   (= (control-characters lf) :lf))
      (throw (ex-info "Expected end of message <CR><LF> bytes."
                      {:read-bytes [(int cr) (int lf)]})))))

(defn- finalize-msg [{:keys [fields] :as msg}]
  (let [talker (subs (first fields) 0 2)
        sentence (subs (first fields) 2 5)]
    (-> msg
        (assoc :talker talker
               :sentence sentence)
        (update :fields subvec 1))))

(defn read-message [in-fn]
  (if-let [start (start-character? (in-fn))]
    (loop [msg {:start start
                :fields [""]}
           ch (in-fn)]

      (let [control (control-characters ch)]

        ;; Checksum
        (if (= :checksum control)
          (let [checksum (read-hex-number in-fn)]
            (if (= checksum (:checksum msg))
              ;; Checksum ends the message, read end and return message
              (do
                (read-end in-fn)
                (finalize-msg msg))
              (throw (ex-info "Checksum mismatch"
                              {:calculated-checksum (:checksum msg)
                               :read-checksum checksum}))))

          (let [msg (update msg :checksum
                            #(if %
                               (bit-xor % (int ch))
                               (int ch)))]

            (cond

              ;; Not a control character, part of field
              (not control)
              (recur (update-in msg [:fields (dec (count (:fields msg)))]
                                str ch)
                     (in-fn))

              ;; Message ends in CR + LF (without a checksum preceeding it)
              (= :cr control)
              (do
                (in-fn ch)
                (read-end in-fn)
                (finalize-msg msg))

              ;; Field delimiter, start a new field
              (= :field control)
              (recur (update msg :fields conj "")
                     (in-fn)))))))))


