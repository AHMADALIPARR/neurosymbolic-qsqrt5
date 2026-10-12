<!--
  Copyright (C) 2026 Ahmad Ali Parr
  SPDX-License-Identifier: AGPL-3.0-only
-->

# neurosymbolic-qsqrt5

Neurosymbolic stack, symbolic layer: **exact arithmetic over Q(√5)**
with **Galois conjugation**, in Clojure. The neural layer proposes
(floats); the symbolic layer disposes (exact proofs of arithmetic).

## Layout

| Path | Role |
|---|---|
| `src/qsqrt5/core.clj` | Q(√5) field: exact `[a b]` rationals, add/mul/inv/norm |
| `src/qsqrt5/galois.clj` | σ: a+b√5 ↦ a−b√5, trace, norm-via-conj, minimal polynomial |
| `src/qsqrt5/bridge.clj` | `snap` (float → exact or nil), `verify`, `certify-op` |
| `test/qsqrt5/` | 18 tests, 4375 assertions — **green on Clojure 1.12.0 / JDK 21** |
| `lean/QSqrt5.lean` | Lean 4 mirror — **machine-checked, `lake build` green, zero `sorry`** |
| `doc/neurosymbolic.org` | Design notes with ob-lean4-executable blocks |

## The math

Q(√5) = {a + b√5 : a,b ∈ Q}. Gal(Q(√5)/Q) ≅ C₂; the nontrivial
automorphism is Galois conjugation σ(a+b√5) = a−b√5. Norm
N(x) = x·σ(x) = a²−5b² is multiplicative; every x satisfies
x² − Tr(x)x + N(x) = 0. All verified by the test suite — exactly,
no floats.

## ob-lean connection

[SNAPKITTYWEST/ob-lean](https://github.com/SNAPKITTYWEST/ob-lean)
(ob-lean4) runs Lean 4 inside Org source blocks. `lean/QSqrt5.lean`
is now fully proved — `lake build` passes with zero `sorry` under
Lean 4.21.0, no Mathlib dependency. The Clojure property tests were
the executable spec; the Lean file is the machine-checked mirror.

## Run tests

```sh
java -cp "lib/clojure.jar:lib/spec.alpha.jar:lib/core.specs.alpha.jar:src:test" \
  clojure.main test/run_tests.clj
```

(`lib/` holds the Clojure jars used to run the suite; not part of
the deliverable.)

## License

AGPL-3.0-only.
