;; Test runner: java -cp ... clojure.main -m run-tests
(require 'qsqrt5.core-test 'qsqrt5.galois-test 'qsqrt5.bridge-test)
(let [{:keys [fail error] :as res}
      (clojure.test/run-tests 'qsqrt5.core-test
                              'qsqrt5.galois-test
                              'qsqrt5.bridge-test)]
  (println res)
  (System/exit (if (zero? (+ fail error)) 0 1)))
