package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.aoc2022.dogunyoye.Day11.Monkey;

public class Day11Test {

    private Monkey[] monkeys;

    private Monkey[] createMonkeys() {
        final Monkey[] monkeys = new Monkey[4];

        monkeys[0] = new Monkey(
            new ArrayDeque<>(List.of(79L, 98L)),
            (worryLevel) -> worryLevel * 19,
            (worryLevel) -> { return (worryLevel % 23 == 0); },
            new int[]{2, 3});

        monkeys[1] = new Monkey(
            new ArrayDeque<>(List.of(54L, 65L, 75L, 74L)),
            (worryLevel) -> worryLevel + 6,
            (worryLevel) -> { return (worryLevel % 19 == 0); },
            new int[]{2, 0});

        monkeys[2] = new Monkey(
            new ArrayDeque<>(List.of(79L, 60L, 97L)),
            (worryLevel) -> worryLevel * worryLevel,
            (worryLevel) -> { return (worryLevel % 13 == 0); },
            new int[]{1, 3});

        monkeys[3] = new Monkey(
            new ArrayDeque<>(List.of(74L)),
            (worryLevel) -> worryLevel + 3,
            (worryLevel) -> { return (worryLevel % 17 == 0); },
            new int[]{0, 1});

        return monkeys;
    }

    @Before
    public void before() throws IOException {
        monkeys = createMonkeys();
    }

    @Test
    public void testPartOne() throws IOException {
        Day11.playKeepAway(monkeys, 20);
        assertEquals(10605, Day11.mostActiveTwoMonkeys(monkeys));
    }

    @Test
    public void testPartTwo() throws IOException {
        final long lcm = Day11.lcm(new long[]{23, 19, 13, 17});
        Day11.playKeepAwayModified(monkeys, lcm, 10000);
        assertEquals(2713310158L, Day11.mostActiveTwoMonkeys(monkeys));
    }
}
