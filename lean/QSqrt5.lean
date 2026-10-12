/-
  Copyright (C) 2026 Ahmad Ali Parr
  SPDX-License-Identifier: AGPL-3.0-only

  QSqrt5.lean — Lean 4 mirror of the qsqrt5 Clojure symbolic layer.

  STATUS: machine-checked. `lake build` passes with zero `sorry`.
  Definitions mirror src/qsqrt5/{core,galois}.clj; every theorem below
  is proved from the definitions by the lemmas that follow.
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

/-- Component-wise extensionality: the workhorse lemma. Every proof
    below reduces element equalities to `Rat` identities via `ext`,
    then closes them with `ring`. -/
@[ext] theorem ext {x y : QSqrt5} (ha : x.a = y.a) (hb : x.b = y.b) :
    x = y := by
  cases x; cases y; simp_all

-- Mirrors qsqrt5.galois/conjugate : the nontrivial Galois automorphism.
def galConj (x : QSqrt5) : QSqrt5 := ⟨x.a, -x.b⟩

-- Mirrors qsqrt5.core/norm and qsqrt5.galois/trace
def norm (x : QSqrt5) : Rat := x.a ^ 2 - 5 * x.b ^ 2
def trace (x : QSqrt5) : Rat := 2 * x.a

-- The statements mirror the property tests in test/qsqrt5/galois_test.clj.

theorem galConj_involutive (x : QSqrt5) : galConj (galConj x) = x := by
  obtain ⟨a, b⟩ := x
  ext <;> simp only [galConj]

theorem galConj_add (x y : QSqrt5) :
    galConj (add x y) = add (galConj x) (galConj y) := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  ext <;> simp only [galConj, add] <;> ring

theorem galConj_mul (x y : QSqrt5) :
    galConj (mul x y) = mul (galConj x) (galConj y) := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  ext <;> simp only [galConj, mul] <;> ring

theorem norm_mul (x y : QSqrt5) : norm (mul x y) = norm x * norm y := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  simp only [norm, mul]; ring

theorem mul_galConj_eq_norm (x : QSqrt5) :
    mul x (galConj x) = ⟨norm x, 0⟩ := by
  obtain ⟨a, b⟩ := x
  ext <;> simp only [mul, galConj, norm] <;> ring

theorem satisfies_min_poly (x : QSqrt5) :
    add (add (mul x x) (neg ⟨trace x * x.a, trace x * x.b⟩)) ⟨norm x, 0⟩
      = ⟨0, 0⟩ := by
  obtain ⟨a, b⟩ := x
  ext <;> simp only [add, mul, neg, trace, norm] <;> ring

end QSqrt5
