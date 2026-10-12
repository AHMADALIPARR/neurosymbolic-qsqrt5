-- Copyright (C) 2026 SnapKitty Collective
-- SPDX-License-Identifier: LicenseRef-NON-AI-MPL-2.0
-- Galois theory of Z[√5], machine-checked.
--
-- We work over Z[√5] (integer pairs), not Q(√5). All six theorems are
-- polynomial identities, so they hold identically over Q(√5); the Clojure
-- layer (`src/qsqrt5/`) uses rationals, the Klong network
-- (`~/workspace/klong-nn/qsqrt5-nn.kg`) uses integer pairs. Z[√5] ↪ Q(√5).
--
-- Lean 4.34 core lacks `ring`; the `grind` tactic closes the nonlinear
-- identities. Zero `sorry`.

/-- An element a + b√5, stored as the pair (a, b). -/
structure QSqrt5 where
  a : Int
  b : Int
deriving Repr, DecidableEq

namespace QSqrt5

/-- Addition: (a+b√5)+(c+d√5) = (a+c)+(b+d)√5. -/
def add (x y : QSqrt5) : QSqrt5 := ⟨x.a + y.a, x.b + y.b⟩

/-- Multiplication: uses (b√5)(d√5) = 5bd. -/
def mul (x y : QSqrt5) : QSqrt5 :=
  ⟨x.a * y.a + 5 * x.b * y.b, x.a * y.b + x.b * y.a⟩

/-- Negation. -/
def neg (x : QSqrt5) : QSqrt5 := ⟨-x.a, -x.b⟩

/-- Component-wise extensionality: the workhorse. -/
@[ext] theorem ext {x y : QSqrt5} (ha : x.a = y.a) (hb : x.b = y.b) :
    x = y := by
  cases x; cases y; simp_all

/-- The nontrivial Galois automorphism: √5 ↦ -√5. -/
def galConj (x : QSqrt5) : QSqrt5 := ⟨x.a, -x.b⟩

/-- Norm: N(a+b√5) = a²-5b². -/
def norm (x : QSqrt5) : Int := x.a * x.a - 5 * x.b * x.b

/-- Trace: Tr(a+b√5) = 2a. -/
def trace (x : QSqrt5) : Int := 2 * x.a

/-- σ is an involution. -/
theorem galConj_involutive (x : QSqrt5) : galConj (galConj x) = x := by
  obtain ⟨a, b⟩ := x
  apply QSqrt5.ext <;> simp [galConj]

/-- σ respects addition. -/
theorem galConj_add (x y : QSqrt5) :
    galConj (add x y) = add (galConj x) (galConj y) := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  apply QSqrt5.ext <;> simp [galConj, add] <;> grind

/-- σ respects multiplication. -/
theorem galConj_mul (x y : QSqrt5) :
    galConj (mul x y) = mul (galConj x) (galConj y) := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  apply QSqrt5.ext <;> simp [galConj, mul] <;> grind

/-- The norm is multiplicative. -/
theorem norm_mul (x y : QSqrt5) : norm (mul x y) = norm x * norm y := by
  obtain ⟨a1, b1⟩ := x; obtain ⟨a2, b2⟩ := y
  simp [norm, mul]; grind

/-- x·σ(x) = N(x) in the base ring. -/
theorem mul_galConj_eq_norm (x : QSqrt5) :
    mul x (galConj x) = ⟨norm x, 0⟩ := by
  obtain ⟨a, b⟩ := x
  apply QSqrt5.ext <;> simp [mul, galConj, norm] <;> grind

/-- Every element satisfies x² - Tr(x)·x + N(x) = 0. -/
theorem satisfies_min_poly (x : QSqrt5) :
    add (add (mul x x) (neg ⟨trace x * x.a, trace x * x.b⟩)) ⟨norm x, 0⟩
      = ⟨0, 0⟩ := by
  obtain ⟨a, b⟩ := x
  apply QSqrt5.ext <;> simp [add, mul, neg, trace, norm] <;> grind

end QSqrt5
