;; Copyright (C) 2026 Ahmad Ali Parr
;; SPDX-License-Identifier: AGPL-3.0-only
(ns qsqrt5.bridge-test
  (:require [clojure.test :refer [deftest is testing]]
            [qsqrt5.core :as q]
            [qsqrt5.bridge :as b]))

(deftest snap-recognizes
  (is (= (b/snap (+ 3.0 (* 2.0 (Math/sqrt 5.0)))) [3 2])
      "3+2√5 recognized")
  (is (= (b/snap (/ 1.0 2.0)) [1/2 0])
      "1/2 recognized")
  (is (= (b/snap (- (Math/sqrt 5.0))) [0 -1])
      "-√5 recognized"))

(deftest snap-refuses
  (is (nil? (b/snap Math/PI))
      "π is not a small Q(√5) element — snap refuses")
  (is (nil? (b/snap 0.123456789))
      "noise refuses"))

(deftest verify-certifies
  (let [x (q/q5 3 2)]
    (is (b/verify x (+ 3.0 (* 2.0 (Math/sqrt 5.0))) 1e-9))
    (is (not (b/verify x 7.5 1e-9)))))

(deftest certify-op-test
  (let [x (q/q5 1 1) y (q/q5 2 -1)
        r (b/certify-op q/qmul * [x y] 1e-9)]
    (is (:ok r))
    (is (q/q= (:exact r) (q/qmul x y)))))
