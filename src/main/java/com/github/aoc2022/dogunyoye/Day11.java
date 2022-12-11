package com.github.aoc2022.dogunyoye;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public class Day11 {

    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }
    
    static long lcm(long[] input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }
    
    static class Monkey {
        private final Queue<Long> items;
        private final Function<Long, Long> operation;
        private final Function<Long, Boolean> test;
        private final int[] monkeysToThrowTo;

        private int numberOfInspects = 0;

        Monkey(Queue<Long> items,
            Function<Long, Long> operation,
            Function<Long, Boolean> test,
            int[] monkeysToThrowTo) {
            this.items = items;
            this.operation = operation;
            this.test = test;
            this.monkeysToThrowTo = monkeysToThrowTo;
        }

        int getNumberOfInspects() {
            return this.numberOfInspects;
        }

        private void catchItem(long item) {
            this.items.add(item);
        }

        private void test(long value, int[] monkeyIds, Monkey[] monkeys) {
            final Monkey m = test.apply(value) ? monkeys[monkeyIds[0]] : monkeys[monkeyIds[1]];
            m.catchItem(value);
        }

        void playRound(Monkey[] monkeys) {
            while(!this.items.isEmpty()) {
                this.numberOfInspects++;
                final long itemWorryLevel = this.items.poll();
                long newWorryLevel = operation.apply(itemWorryLevel);
                newWorryLevel /= 3;
                test(newWorryLevel, this.monkeysToThrowTo, monkeys);
            }
        }

        void playRoundModified(Monkey[] monkeys, long lcm) {
            while(!this.items.isEmpty()) {
                this.numberOfInspects++;
                final long itemWorryLevel = this.items.poll();
                long newWorryLevel = operation.apply(itemWorryLevel);
                newWorryLevel %= lcm;
                test(newWorryLevel, this.monkeysToThrowTo, monkeys);
            }
        }
    }

    static Monkey[] createMonkeys() {
        final Monkey[] monkeys = new Monkey[8];

        monkeys[0] = new Monkey(
            new ArrayDeque<>(List.of(80L)),
            (worryLevel) -> worryLevel * 5,
            (worryLevel) -> { return (worryLevel % 2 == 0); },
            new int[]{4, 3});
        
        monkeys[1] = new Monkey(
            new ArrayDeque<>(List.of(75L, 83L, 74L)),
            (worryLevel) -> worryLevel + 7,
            (worryLevel) -> { return (worryLevel % 7 == 0); },
            new int[]{5, 6});
        
        monkeys[2] = new Monkey(
            new ArrayDeque<>(List.of(86L, 67L, 61L, 96L, 52L, 63L, 73L)),
            (worryLevel) -> worryLevel + 5,
            (worryLevel) -> { return (worryLevel % 3 == 0); },
            new int[]{7, 0});

        monkeys[3] = new Monkey(
            new ArrayDeque<>(List.of(85L, 83L, 55L, 85L, 57L, 70L, 85L, 52L)),
            (worryLevel) -> worryLevel + 8,
            (worryLevel) -> { return (worryLevel % 17 == 0); },
            new int[]{1, 5});

        monkeys[4] = new Monkey(
            new ArrayDeque<>(List.of(67L, 75L, 91L, 72L, 89L)),
            (worryLevel) -> worryLevel + 4,
            (worryLevel) -> { return (worryLevel % 11 == 0); },
            new int[]{3, 1});

        monkeys[5] = new Monkey(
            new ArrayDeque<>(List.of(66L, 64L, 68L, 92L, 68L, 77L)),
            (worryLevel) -> worryLevel * 2,
            (worryLevel) -> { return (worryLevel % 19 == 0); },
            new int[]{6, 2});

        monkeys[6] = new Monkey(
            new ArrayDeque<>(List.of(97L, 94L, 79L, 88L)),
            (worryLevel) -> worryLevel * worryLevel,
            (worryLevel) -> { return (worryLevel % 5 == 0); },
            new int[]{2, 7});

        monkeys[7] = new Monkey(
            new ArrayDeque<>(List.of(77L, 85L)),
            (worryLevel) -> worryLevel + 6,
            (worryLevel) -> { return (worryLevel % 13 == 0); },
            new int[]{4, 0});

        return monkeys;
    }

    static void playKeepAway(Monkey[] monkeys, int rounds) {
        while(rounds != 0) {
            for (final Monkey m : monkeys) {
                m.playRound(monkeys);
            }
            rounds--;
        }
    }

    static void playKeepAwayModified(Monkey[] monkeys, long lcm, int rounds) {
        while(rounds != 0) {
            for (final Monkey m : monkeys) {
                m.playRoundModified(monkeys, lcm);
            }
            rounds--;
        }
    }

    static long mostActiveTwoMonkeys(Monkey[] monkeys) {
        final long[] inspectsPerMonkey = new long[8];
        int i = 0;
        for (final Monkey m : monkeys) {
            inspectsPerMonkey[i] = m.getNumberOfInspects();
            i++;
        }

        Arrays.sort(inspectsPerMonkey);
        return inspectsPerMonkey[7] * inspectsPerMonkey[6];
    }

    public static void main(String[] args) {
        final Monkey[] monkeys = createMonkeys();
        playKeepAway(monkeys, 20);
        System.out.println("Part 1: " + mostActiveTwoMonkeys(monkeys));

        final Monkey[] monkeys2 = createMonkeys();
        final long lcm = lcm(new long[]{2, 7, 3, 17, 11, 19, 5, 13});
        playKeepAwayModified(monkeys2, lcm, 10000);
        System.out.println("Part 2: " + mostActiveTwoMonkeys(monkeys2));
    }
}
