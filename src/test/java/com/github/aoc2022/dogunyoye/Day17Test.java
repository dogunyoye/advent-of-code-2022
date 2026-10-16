package com.github.aoc2022.dogunyoye;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Day17Test {
    private static String commands;

    @BeforeAll
    public static void setUp() throws IOException {
        commands = Files.readAllLines(Path.of("src/test/resources/Day17TestInput.txt"))
                .get(0);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(3068, Day17.findGreatestHeightFromSimulatingFallingRocks(commands, 2022));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(
                1514285714288L, Day17.findGreatestHeightFromSimulatingFallingRocks(commands, Day17.PART_TWO_ROCKS));
    }
}
