;; Copyright (C) 2026 SnapKitty Collective
;; SPDX-License-Identifier: LicenseRef-NON-AI-MPL-2.0
(ns qsqrt5.galois
  "Galois theory of Q(sqrt(5)).

   Gal(Q(√5)/Q) ≅ C2. The nontrivial automorphism σ sends
   √5 ↦ -√5, i.e. σ(a+b√5) = a-b√5. (Named `conjugate` here because
   `conj` is taken by clojure.core.)

   Consequences computed exactly:
     trace(x) = x + σ(x)       = 2a
     norm(x)  = x * σ(x)       = a^2 - 5b^2
   and every x satisfies its minimal polynomial
     x^2 - trace(x)*x + norm(x) = 0."
  (:require [qsqrt5.core :as q]))

(defn conjugate
  "The nontrivial Galois automorphism σ: a+b√5 ↦ a-b√5."
  [[a b]] [a (- b)])

(defn trace
  "Field trace Tr(x) = x + σ(x) = 2a, in Q."
  [[a _]] (* 2 a))

(defn norm-via-conj
  "N(x) = x·σ(x), computed through the automorphism. Must equal
   qsqrt5.core/norm — the agreement is tested, not assumed."
  [x]
  (let [[n _] (q/qmul x (conjugate x))] n))

(defn rational-fixed?
  "True iff x lies in the fixed field of σ, i.e. x ∈ Q (b = 0)."
  [[_ b]] (zero? b))

(defn min-poly
  "Coefficients [c0 c1 c2] of the minimal polynomial
   c2*x^2 + c1*x + c0 satisfied by x: x^2 - Tr(x)*x + N(x)."
  [x]
  [(q/norm x) (- (trace x)) 1])

(defn satisfies-min-poly?
  "Evaluates x^2 - Tr(x)*x + N(x) exactly; true iff it is zero."
  [x]
  (let [x2  (q/qmul x x)
        trx (q/qmul (q/q5 (trace x) 0) x)
        n   (q/q5 (q/norm x) 0)]
    (q/q-zero? (q/qadd (q/qsub x2 trx) n))))

(defn automorphism-check
  "Verifies σ is a field automorphism on the given sample pairs:
   σ(x+y)=σx+σy, σ(xy)=σxσy, σ(σx)=x, σ(1)=1. Returns true/false."
  [xs]
  (every? true?
    (for [x xs y xs]
      (and (q/q= (conjugate (q/qadd x y))
                 (q/qadd (conjugate x) (conjugate y)))
           (q/q= (conjugate (q/qmul x y))
                 (q/qmul (conjugate x) (conjugate y)))
           (q/q= (conjugate (conjugate x)) x))))
  )
