# klong/ — Galois-equivariant array neural network in Klong

Exact bignum pair arithmetic for **Z[√5]**, rank-polymorphic dense layers,
zero explicit loops. Dimensional evaluation only.

## Design

**Representation.** A Z[√5] element is a 2-vector `[a b]` = a+b√5 over
exact integers (Klong bignums). A layer vector of n neurons is an n×2
matrix. A batch of m vectors is m×n×2. No floats anywhere.

**Operators.** `qadd qmul qconj qnorm qtrace` port the Clojure exact
arithmetic (`src/qsqrt5/`). `qdot` folds `qadd` over pairwise `qmul` —
inner product with no loops. `qact` is the Galois gate: keep x iff
N(x) ≥ 0 else 0, exact integer comparison, conjugation-invariant.

**Rank polymorphism.** One operator, `qfwd`, changes behavior from shape:
rank-2 input (n×2) → single forward pass; rank-3 (m×n×2) → batch map
via `:\` (each-left). No static dispatch, no keywords — `#^` (rank)
drives it.

**Dimensional evaluation.** The forward pass only reduces rank:
`qfwd` → `qfv` → `qdot` → `qadd`/`qmul`. Control flow is implicit in
the adverbs (`'` each, `f'b` each-2, `f:\` each-left, `f/` fold).

## Lean axioms as runtime invariants

The proved Galois properties (`lean/QSqrt5.lean`) are checked on live
values — all must be all-ones:

- `qconj(qconj(x)) = x` (involution)
- `qconj(x+y) = qconj(x)+qconj(y)`, `qconj(x*y) = qconj(x)*qconj(y)`
- `N(x*y) = N(x)*N(y)` (norm multiplicative)

## Galois symmetry (theorem, verified)

Because σ is a homomorphism and the gate is σ-invariant, conjugating
**params and input** conjugates the output:

    qfwd(σP; σX) = σ(qfwd(P; X))

Verified for single and batch. (Input-only conjugation does *not*
hold — the weights must be conjugated too, since σ distributes over
the products with them. The check caught this; the claim was corrected.)

## Toolchain

Klong is Nils M. Holm's array language (pure ANSI C). Build it:

```sh
curl -O https://t3x.org/klong/klong20221212.tgz
tar xzf klong20221212.tgz && cd klong && make
```

Then run the network:

```sh
./kg qsqrt5-nn.kg
```

Demo network 3→2→1, exact integer arithmetic throughout. The binary
and image are build artifacts — not committed; build from source.
