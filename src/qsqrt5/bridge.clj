;; Copyright (C) 2026 SnapKitty Collective
;; SPDX-License-Identifier: LicenseRef-NON-AI-MPL-2.0
(ns qsqrt5.bridge
  "Neurosymbolic bridge: the interface between the neural layer
   (floats, approximations, proposals) and the symbolic layer
   (exact Q(√5) arithmetic).

   Direction neural → symbolic: `snap` recognizes a double as an
   exact a+b√5 with small coefficients, or refuses (nil).
   Direction symbolic → neural: `verify` certifies a neural float
   against an exact computation within tolerance.

   Rule of the bridge: floats never flow INTO exact arithmetic
   except through `snap`, and `snap` never succeeds silently —
   it returns nil when no small exact form fits."
  (:require [qsqrt5.core :as q]))

(def ^:private sqrt5d (Math/sqrt 5.0))

(defn snap
  "Recognize double d as an exact a+b√5 with |numerators| <= nlim and
   denominators <= dlim. Returns [a b] of exact rationals, or nil.
   Bounded search: honest about what it can and cannot recognize."
  ([d] (snap d 12 8 1e-9))
  ([d nlim dlim tol]
   (let [cands (for [db (range 1 (inc dlim))
                     nb (range (- nlim) (inc nlim))
                     da (range 1 (inc dlim))
                     na (range (- nlim) (inc nlim))
                     :let [a (/ na da) b (/ nb db)
                           err (Math/abs (- (+ (double a)
                                               (* (double b) sqrt5d))
                                            d))]
                     :when (<= err tol)]
                 [a b])]
     (first (sort-by (fn [[a b]] (+ (Math/abs (double a))
                                    (Math/abs (double b))))
                     cands)))))

(defn verify
  "Certify a neural float `approx` against the exact element `x`:
   true iff |to-double(x) - approx| <= tol."
  [x approx tol]
  (<= (Math/abs (- (q/to-double x) approx)) tol))

(defn certify-op
  "Run exact op on exact inputs, run float-op on their double values,
   certify agreement within tol. Returns {:exact :approx :ok}."
  [exact-op float-op xs tol]
  (let [exact  (apply exact-op xs)
        approx (apply float-op (map q/to-double xs))]
    {:exact exact :approx approx :ok (verify exact approx tol)}))
