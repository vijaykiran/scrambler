(ns scrambler.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [scrambler.core-test]))

(doo-tests 'scrambler.core-test)

