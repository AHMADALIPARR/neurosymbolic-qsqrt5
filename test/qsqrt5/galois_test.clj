;; Copyright (C) 2026 Ahmad Ali Parr
;; SPDX-License-Identifier: AGPL-3.0-only
(ns qsqrt5.galois-test
  (:require [clojure.test :refer [deftest is testing]]
            [qsqrt5.core :as q]
            [qsqrt5.galois :as g]))

(def samples
  [(q/q5 0 0) (q/q5 1 0) (q/q5 0 1) (q/q5 3 2) (q/q5 -1 4)
   (q/q5 1/2 -3/4) (q/q5 7 -7) (q/q5 -5/3 11/7)])

(deftest conjugation-involution
  (doseq [x samples]
    (is (q/q= (g/conjugate (g/conjugate x)) x)
        "σ² = id")))

(deftest conjugation-homomorphism
  (doseq [x samples y samples]
    (is (q/q= (g/conjugate (q/qadd x y))
              (q/qadd (g/conjugate x) (g/conjugate y)))
        "σ(x+y) = σx+σy")
    (is (q/q= (g/conjugate (q/qmul x y))
              (q/qmul (g/conjugate x) (g/conjugate y)))
        "σ(xy) = σxσy")))

(deftest automorphism-checker
  (is (true? (g/automorphism-check samples))))

(deftest norm-agreement
  (doseq [x samples]
    (is (== (g/norm-via-conj x) (q/norm x))
        "x·σ(x) as element equals norm")))

(deftest trace-is-sum
  (doseq [x samples]
    (is (q/q= (q/q5 (g/trace x) 0) (q/qadd x (g/conjugate x)))
        "Tr(x) = x + σ(x)")))

(deftest fixed-field
  (is (g/rational-fixed? (q/q5 3/4 0)))
  (is (not (g/rational-fixed? (q/q5 3/4 1/9))))
  (doseq [x samples]
    (is (= (g/rational-fixed? x)
           (q/q= (g/conjugate x) x))
        "fixed by σ iff in Q")))

(deftest minimal-polynomial
  (doseq [x samples]
    (is (g/satisfies-min-poly? x)
        (str "x satisfies x² - Tr(x)x + N(x) = 0 for " x)))
  (testing "√5 has minimal polynomial x² - 5"
    (is (= (g/min-poly q/sqrt5) [-5 0 1]))))
