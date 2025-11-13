# Implementation Report — Knuth–Morris–Pratt (KMP)

## Overview

This project contains a from-scratch implementation of the Knuth–Morris–Pratt
(KMP) string matching algorithm in Java. The implementation focuses on clarity
and correctness for educational and testing purposes.

## Implementation details

- `KMP.java` implements two main methods:
  - `computeLPS(String pattern)`: constructs the LPS (longest proper prefix
    which is also a suffix) array for the pattern in O(m) time and O(m) space,
    where m is the pattern length.
  - `search(String text, String pattern)`: uses the LPS array to find all
    occurrences of the pattern in the text in O(n) time, where n is the text
    length. The method returns a list of 0-based starting indices.

- The code handles several edge cases:
  - `null` text or `null` pattern returns an empty result list.
  - empty pattern is defined to match at every position `0..n` (this is an
    intentional design choice and is documented in code).
  - overlapping occurrences are correctly found because KMP updates the
    pattern index using the LPS table after each full match.

## Testing

The `Main` runner performs three example tests to observe behavior across
input sizes. The measured average times below are taken from the program's
output (averaged over 5 runs as performed by `Main`).

1) Short test
   - Text: `"ababa"`
   - Pattern: `"aba"`
   - Result: matches at indices `[0, 2]`
   - Measured average time (over 5 runs): `0.002480 ms`

2) Medium test
   - Text: `"ababababacababababacababababac"` (length 30)
   - Pattern: `"abababac"` (length 8)
   - Result: matches at indices `[2, 12, 22]`
   - Measured average time (over 5 runs): `0.005220 ms`

3) Long test
   - Text: 10000 repetitions of `"abacaba"` (length 70000)
   - Pattern: `"abacabaabac"` (length 11)
   - Result: `9999` matches; first indices: `[0, 7, 14, 21, 28, 35, ...]`
   - Measured average time (over 5 runs): `2.923780 ms`

## Notes on benchmarking

- For short inputs, time measurements can be noisy because of JVM/warm-up
  overhead and the resolution of timing APIs; the very small average times
  (fractions of a millisecond) reflect that the measured region is tiny and
  dominated by CPU timer resolution and small overheads.
- The long test demonstrates KMP's linear scaling: even at 70k characters the
  measured average time is small (≈2.9 ms on the test machine), showing the
  method performs efficiently in practice for longer inputs.

## Complexity analysis

- **Time complexity**: O(n + m) where n = length of the text, m = length of the
  pattern. Building the LPS array is O(m). Scanning the text with KMP is O(n)
  because each character in the text and pattern is advanced or reset a small
  number of times in total.

- **Space complexity**: O(m) additional space for the LPS array, plus O(k) for the
  result list of matches where k is the number of matches. Aside from input
  and the output list, memory overhead is constant.

## Conclusions and remarks

- The KMP algorithm is ideal when you need exact pattern matching with worst-
  case linear guaranteed time. It performs particularly well on long inputs
  and when many partial matches exist because the LPS table avoids repeated
  rescanning.
- The provided implementation is clear and easily testable. It can be extended
  with unit tests (JUnit) or integrated into larger projects.

## Files included in this repository

- `src/main/java/org/example/KMP.java` — algorithm implementation
- `src/main/java/org/example/Main.java` — test runner and simple benchmarks
- `sample_output.txt` — example program output (short/medium/long runs)
- `report.txt` — a plain-text copy of the same report

## Build and run

Requirements:
- JDK (tested with Java 8+)
- Maven (optional)

Using Maven (recommended if available):

```cmd
mvn package
java -cp target\BonusTask-1.0-SNAPSHOT.jar org.example.Main
```

Without Maven (javac/java):

```cmd
javac -d out src\main\java\org\example\KMP.java src\main\java\org\example\Main.java
java -cp out org.example.Main
```

If you want additional artifacts (JUnit tests, PDF report, JMH benchmarks), tell me which and I will add them.
