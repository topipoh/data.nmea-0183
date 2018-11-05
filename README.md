[![CircleCI](https://circleci.com/gh/tatut/data.nmea-0183.svg?style=svg)](https://circleci.com/gh/tatut/data.nmea-0183)

# NMEA 0183 parser

This project is a simple parser from NMEA 0183 messages into Clojure data.

The fields and maps are defined with Clojure spec.

The parsing is IO agnostic and can be used with any source that can provide successive characters.
