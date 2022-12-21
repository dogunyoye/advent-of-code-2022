package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day16Test {
    private static List<String> valves;

    @BeforeClass
    public static void setUp() throws IOException {
        valves = Files.readAllLines(Path.of("src/test/resources/Day16TestInput.txt"));
        Day16.parseValves(valves);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(1651, Day16.findMaximumPressureIn30Mins());
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(1707, Day16.findMaximumPressureWithElephant());
    }
}
