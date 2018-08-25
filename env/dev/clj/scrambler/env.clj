(ns scrambler.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [scrambler.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[scrambler started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[scrambler has shut down successfully]=-"))
   :middleware wrap-dev})
