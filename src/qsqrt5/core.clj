;; Copyright (C) 2026 Ahmad Ali Parr
;; SPDX-License-Identifier: AGPL-3.0-only
(ns qsqrt5.core
  "Exact arithmetic over the quadratic number field Q(sqrt(5)).

   An element a + b*sqrt(5), a b in Q, is represented as the vector
   [a b] of Clojure exact rationals. Every operation is exact:
   no floating point appears anywhere in this namespace.

   Field structure:
     (a+b√5) + (c+d√5) = (a+c) + (b+d)√5
     (a+b√5) * (c+d√5) = (ac+5bd) + (ad+bc)√5
     1/(a+b√5)         = (a-b√5) / (a^2 - 5b^2)
   The denominator a^2-5b^2 vanishes only at (0,0), since sqrt(5)
   is irrational over Q.")

(defn q5
  "Constructor: (q5 a b) = a + b*sqrt(5). Coerces inputs to exact rationals."
  [a b]
  [(rationalize a) (rationalize b)])

(def q-zero [0 0])
(def q-one  [1 0])
(def sqrt5  [0 1])

(defn q-zero? [[a b]] (and (zero? a) (zero? b)))
(defn q-one?  [[a b]] (and (== a 1) (zero? b)))

(defn q= [[a1 b1] [a2 b2]]
  "Exact equality."
  (and (== a1 a2) (== b1 b2)))

(defn qneg [[a b]] [(- a) (- b)])

(defn qadd [[a1 b1] [a2 b2]] [(+ a1 a2) (+ b1 b2)])
(defn qsub [[a1 b1] [a2 b2]] [(- a1 a2) (- b1 b2)])

(defn qmul [[a1 b1] [a2 b2]]
  [(+ (* a1 a2) (* 5 b1 b2))
   (+ (* a1 b2) (* a2 b1))])

(defn norm
  "Field norm N(a+b√5) = a^2 - 5b^2, in Q. Multiplicative; zero iff the
   element is zero (sqrt(5) is irrational over Q)."
  [[a b]]
  (- (* a a) (* 5 b b)))

(defn qinv
  "Multiplicative inverse via the norm: 1/x = conjugate(x)/N(x).
   Throws ArithmeticException on zero."
  [x]
  (let [n (norm x)]
    (when (zero? n)
      (throw (ArithmeticException. "qinv of zero")))
    (let [[a b] x] [(/ a n) (/ (- b) n)])))

(defn qdiv [x y] (qmul x (qinv y)))

(defn qpow
  "x^n for integer n >= 0."
  [x n]
  (assert (and (integer? n) (>= n 0)) "qpow needs n >= 0 integer")
  (loop [acc q-one, base x, e n]
    (if (zero? e) acc
        (recur (if (odd? e) (qmul acc base) acc)
               (qmul base base)
               (quot e 2)))))

(defn to-double
  "INEXACT bridge to the neural side: evaluates a+b√5 as a double.
   The only inexact function in the symbolic layer; quarantined here
   so exactness everywhere else is auditable."
  [[a b]]
  (+ (double a) (* (double b) (Math/sqrt 5.0))))
