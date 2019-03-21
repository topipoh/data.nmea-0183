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
  "Returns an input stream source. Closes input if end of stream is reached."
  [^InputStream in]
  (pushback-fn #(let [b (.read in)]
                  (if (= -1 b)
                    (do (try-close in)
                        (throw (EOFException.)))
                    (char b)))))
