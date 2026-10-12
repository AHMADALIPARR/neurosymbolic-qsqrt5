;; Copyright (C) 2026 Ahmad Ali Parr
;; SPDX-License-Identifier: AGPL-3.0-only
(ns qsqrt5.core-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [qsqrt5.core :as q]))

(def samples
  [(q/q5 0 0) (q/q5 1 0) (q/q5 0 1) (q/q5 3 2) (q/q5 -1 4)
   (q/q5 1/2 -3/4) (q/q5 7 -7) (q/q5 -5/3 11/7)])

(deftest constructor
  (is (q/q= (q/q5 3 2) [3 2]))
  (is (q/q= (q/q5 1/2 0) [1/2 0]))
  (is (q/q-zero? q/q-zero))
  (is (q/q-one? q/q-one))
  (is (q/q= q/sqrt5 [0 1])))

(deftest ring-axioms
  (testing "additive group"
    (doseq [x samples y samples z samples]
      (is (q/q= (q/qadd x (q/qadd y z)) (q/qadd (q/qadd x y) z)))
      (is (q/q= (q/qadd x y) (q/qadd y x)))
      (is (q/q= (q/qadd x q/q-zero) x))
      (is (q/q= (q/qadd x (q/qneg x)) q/q-zero))))
  (testing "multiplicative monoid"
    (doseq [x samples y samples z samples]
      (is (q/q= (q/qmul x (q/qmul y z)) (q/qmul (q/qmul x y) z)))
      (is (q/q= (q/qmul x y) (q/qmul y x)))
      (is (q/q= (q/qmul x q/q-one) x))))
  (testing "distributivity"
    (doseq [x samples y samples z samples]
      (is (q/q= (q/qmul x (q/qadd y z))
                (q/qadd (q/qmul x y) (q/qmul x z)))))))

(deftest sqrt5-squared
  (is (q/q= (q/qmul q/sqrt5 q/sqrt5) (q/q5 5 0))
      "√5^2 = 5 exactly"))

(deftest inverses
  (doseq [x (remove q/q-zero? samples)]
    (is (q/q= (q/qmul x (q/qinv x)) q/q-one)
        (str "x * x^-1 = 1 for " x))
    (is (q/q= (q/qdiv x x) q/q-one)))
  (is (thrown? ArithmeticException (q/qinv q/q-zero))))

(deftest norm-basics
  (is (== (q/norm (q/q5 3 2)) (- 9 20)))
  (is (== (q/norm q/q-zero) 0))
  (is (== (q/norm q/q-one) 1))
  (doseq [x samples y samples]
    (is (== (q/norm (q/qmul x y)) (* (q/norm x) (q/norm y)))
        "norm is multiplicative")))

(deftest pow-test
  (is (q/q= (q/qpow q/sqrt5 2) (q/q5 5 0)))
  (is (q/q= (q/qpow (q/q5 1 1) 0) q/q-one))
  (doseq [x (remove q/q-zero? samples)]
    (is (q/q= (q/qpow x 3) (q/qmul x (q/qmul x x))))))

(deftest to-double-sane
  (is (< (Math/abs (- (q/to-double (q/q5 3 2))
                      (+ 3.0 (* 2.0 (Math/sqrt 5.0)))))
         1e-12)))
