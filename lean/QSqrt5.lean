/-
  Copyright (C) 2026 Ahmad Ali Parr
  SPDX-License-Identifier: AGPL-3.0-only

  QSqrt5.lean — Lean 4 mirror of the qsqrt5 Clojure symbolic layer.

  STATUS: UNVERIFIED. Definitions and theorem statements mirror
  src/qsqrt5/{core,galois}.clj exactly; proofs are NOT machine-checked
  here (no Lean toolchain on the build host). Execute via ob-lean4
  org-babel blocks (cf. doc/neurosymbolic.org) on a machine with
  Lean 4 + ob-lean4 installed. `sorry` below marks exactly what
  remains to be discharged.
-/

-- Q(√5) as pairs of rationals. Mirrors qsqrt5.core/q5.
structure QSqrt5 where
  a : Rat
  b : Rat
deriving Repr, DecidableEq

namespace QSqrt5

-- Mirrors qsqrt5.core/qadd, qsqrt5.core/qmul
def add (x y : QSqrt5) : QSqrt5 := ⟨x.a + y.a, x.b + y.b⟩
def mul (x y : QSqrt5) : QSqrt5 :=
  ⟨x.a * y.a + 5 * x.b * y.b, x.a * y.b + x.b * y.a⟩
def neg (x : QSqrt5) : QSqrt5 := ⟨-x.a, -x.b⟩

-- Mirrors qsqrt5.galois/conjugate : the nontrivial Galois automorphism.
def galConj (x : QSqrt5) : QSqrt5 := ⟨x.a, -x.b⟩

-- Mirrors qsqrt5.core/norm and qsqrt5.galois/trace
def norm (x : QSqrt5) : Rat := x.a ^ 2 - 5 * x.b ^ 2
def trace (x : QSqrt5) : Rat := 2 * x.a

-- The statements below mirror the property tests in
-- test/qsqrt5/galois_test.clj. Each `sorry` is a proof obligation
-- for the ob-lean4 session, not an established fact.

theorem galConj_involutive (x : QSqrt5) : galConj (galConj x) = x := by
  sorry

theorem galConj_add (x y : QSqrt5) :
    galConj (add x y) = add (galConj x) (galConj y) := by
  sorry

theorem galConj_mul (x y : QSqrt5) :
    galConj (mul x y) = mul (galConj x) (galConj y) := by
  sorry

theorem norm_mul (x y : QSqrt5) : norm (mul x y) = norm x * norm y := by
  sorry

theorem mul_galConj_eq_norm (x : QSqrt5) :
    mul x (galConj x) = ⟨norm x, 0⟩ := by
  sorry

theorem satisfies_min_poly (x : QSqrt5) :
    add (add (mul x x) (neg ⟨trace x * x.a, trace x * x.b⟩)) ⟨norm x, 0⟩
      = ⟨0, 0⟩ := by
  sorry

end QSqrt5
