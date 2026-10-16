# advent-of-code-2022

Advent of Code 2022 solutions in Java!

All questions can be accessed here: https://adventofcode.com/2022

## Prerequisites

- Java 17+
- Maven 3.8+

## Build and test

```sh
mvn clean test
```

## Run a solution

```sh
mvn exec:java -Dexec.mainClass="com.github.aoc2022.dogunyoye.DayXX"
```

where `XX` denotes the chosen day - `Day01` for the 1st day and so on.

Each day reads its personal puzzle input from `src/main/resources/DayXX.txt`.
These files are git-ignored; download your own input from Advent of Code
and place it there. Sample inputs used by the tests live in
`src/test/resources/`.

## Project layout

```text
src/main/java/com/github/aoc2022/dogunyoye/  # one class per day (Day01-Day25)
src/main/resources/                          # personal puzzle inputs (git-ignored)
src/test/java/...                            # JUnit tests per day
src/test/resources/                          # sample inputs for tests
```