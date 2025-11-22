package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 {

    private enum Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        EQUALITY
    }

    static class Evaluation {
        String leftMonkey;
        String rightMonkey;
        Operation op;

        Evaluation(String leftMonkey, String rightMonkey, Operation op) {
            this.leftMonkey = leftMonkey;
            this.rightMonkey = rightMonkey;
            this.op = op;
        }

        @Override
        public String toString() {
            return "Evaluation: [" + this.leftMonkey + " " + this.op + " " + this.rightMonkey + "]";
        }
    }

    static void parseMonkeys(List<String> data, Map<String, Long> solved, Map<String, Evaluation> evaluate) {
        for (final String line : data) {
            final String[] parts = line.split(": ");
            final String monkeyName = parts[0];
            final String operationOrValue = parts[1];

            if (operationOrValue.matches("-?(0|[1-9]\\d*)")) {
                // a digit
                solved.put(monkeyName, Long.parseLong(operationOrValue));
            } else {
                final String[] operationParts = operationOrValue.split(" ");
                final String leftMonkey = operationParts[0];
                final String operation = operationParts[1];
                final String rightMonkey = operationParts[2];
                Operation op = null;

                switch(operation) {
                    case "+":
                        op = Operation.ADD;
                        break;
                    case "-":
                        op = Operation.SUBTRACT;
                        break;
                    case "*":
                        op = Operation.MULTIPLY;
                        break;
                    case "/":
                        op = Operation.DIVIDE;
                        break;
                    default:
                        throw new RuntimeException("Unknown operation: " + operation);
                }

                evaluate.put(monkeyName, new Evaluation(leftMonkey, rightMonkey, op));
            }
        }
    }

    private static long evaluateMonkey(String name, Map<String, Long> solved, Map<String, Evaluation> evaluate) {
        final Long value = solved.get(name);
        if (value != null) {
            return value;
        }

        final Evaluation e = evaluate.get(name);
        final long left = evaluateMonkey(e.leftMonkey, solved, evaluate);
        final long right = evaluateMonkey(e.rightMonkey, solved, evaluate);

        long result = 0;
        switch(e.op) {
            case ADD:
                result = left + right;
                break;
            case SUBTRACT:
                result = left - right;
                break;
            case MULTIPLY:
                result = left * right;
                break;
            case DIVIDE:
                result = left / right;
                break;
            default:
                throw new RuntimeException("Unknown operation: " + e.op);
        }

        solved.put(name, result);
        return result;
    }

    private static long evaluateMonkeyWithEqualityCheck(String name,
        Map<String, Long> solved,
        Map<String, Evaluation> evaluate,
        long[] rootNumbers,
        BitSet invalidSolution) {

        final Long value = solved.get(name);
        if (value != null) {
            return value;
        }

        final Evaluation e = evaluate.get(name);
        final long left = evaluateMonkeyWithEqualityCheck(e.leftMonkey, solved, evaluate, rootNumbers, invalidSolution);
        final long right = evaluateMonkeyWithEqualityCheck(e.rightMonkey, solved, evaluate, rootNumbers, invalidSolution);

        long result = 0;
        switch(e.op) {
            case ADD:
                result = left + right;
                break;
            case SUBTRACT:
                result = left - right;
                break;
            case MULTIPLY:
                result = left * right;
                break;
            case DIVIDE:
                if (left % right != 0) {
                    invalidSolution.set(0);
                }
                result = left / right;
                break;
            case EQUALITY:
                rootNumbers[0] = left;
                rootNumbers[1] = right;
                break;
            default:
                throw new RuntimeException("Unknown operation: " + e.op);
        }

        solved.put(name, result);
        return result;
    }

    public static long findRootMonkeyNumber(List<String> data) {
        final Map<String, Long> solved = new HashMap<>();
        final Map<String, Evaluation> evaluate = new HashMap<>();
        parseMonkeys(data, solved, evaluate);

        return evaluateMonkey("root", solved, evaluate);
    }

    public static long findNumberToPassRootEqualityCheck(List<String> data) {

        long low = 0L;
        long high = 1000000000000000L;

        boolean directionDetermined = false;
        boolean invert = false;

        while (low <= high) {
            final long mid = low  + ((high - low) / 2L);

            final Map<String, Long> solved = new HashMap<>();
            final Map<String, Evaluation> evaluate = new HashMap<>();
            final BitSet invalidSolution = new BitSet();
            parseMonkeys(data, solved, evaluate);
    
            final Evaluation rootEvaluation = evaluate.get("root");
            rootEvaluation.op = Operation.EQUALITY;

            solved.put("humn", mid);
            final long[] rootNumbers = new long[2];

            evaluateMonkeyWithEqualityCheck("root", solved, evaluate, rootNumbers, invalidSolution);

            if (!directionDetermined) {
                if (rootNumbers[0] > rootNumbers[1]) {
                    invert = true;
                }       
                directionDetermined = true;
            }

            if (rootNumbers[0] > rootNumbers[1]) {
                if (invert) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else if (rootNumbers[0] < rootNumbers[1]) {
                if (invert) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            } else {
                if (invalidSolution.get(0)) {
                    // this solution is invalid due to non-integer division
                    if (invert) {
                        low = mid;
                    } else {
                        high = mid;
                    }
                } else {
                    return mid;
                }
            }
        }

        throw new RuntimeException("Unable to find suitable value for humn");
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day21.txt"));

        System.out.println("Part 1: " + findRootMonkeyNumber(data));
        System.out.println("Part 2: " + findNumberToPassRootEqualityCheck(data));
    }
}
