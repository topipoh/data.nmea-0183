(ns data.nmea-0183.input
  "IO facilities for reading from input sources.

  The message reader takes an input source that is a function.
  The function must return successive characters from the source when called with 0 arguments.
  The function must support pushback of 1 character when called with 1 argument."
  (:import (java.io InputStream EOFException)))

(set! *warn-on-reflection* true)

(defn- pushback-fn [fun]
  (let [pushback (volatile! nil)]
     (fn
       ([]
        (if-let [val @pushback]
          (do
            (vreset! pushback nil)
            val)
          (fun)))
       ([pushback-val]
        (vreset! pushback pushback-val)))))

(defn- try-close [^InputStream in]
  (try
    (.close in)
    (catch Exception e)))

(defn input-stream
  "Returns an input stream source.

  If :throw-on-eof? option is true (default) closes input if end of
  stream is reached and throws EOFException.

  If :throw-on-eof? is false, the :eof-value (default nil) is returned.

  If :trace-fn function is given, all read input is echoed to the
  given function as int value."

  ([^InputStream in]
   (input-stream in {}))
  ([^InputStream in opts]
   (let [opts (merge {:throw-on-eof? true
                      :eof-value nil
                      :trace-fn nil}
                     opts)
         {:keys [throw-on-eof? eof-value trace-fn]} opts]
     (pushback-fn #(let [b (.read in)]
                     (when trace-fn
                       (trace-fn b))
                     (if (= -1 b)
                       (if throw-on-eof?
                         (do (try-close in)
                             (throw (EOFException.)))
                         eof-value)
                       (char b)))))))
