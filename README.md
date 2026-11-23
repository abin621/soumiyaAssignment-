# Artifacts for the CSCU9E5 Assignment 2# CSCU9E5 Assignment 2: Traffic Lights Statechart Specifications

This repository contains `clj-statecharts` specifications, Mermaid `stateDiagram-v2` diagrams, and verification tests for three traffic light configurations:

- `one-way-without-ctrl`: Single signal following the UK signal cycle[cite: 1, 2].
- `one-way`: Single controlled signal conforming to R1–R10[cite: 1, 2].
- `three-way`: Three coordinated signals (`:east`, `:north`, `:south`) conforming to R1–R10[cite: 1, 2].

## Running Tests

Run all verification tests with Kaocha:

```bash
clojure -M:test