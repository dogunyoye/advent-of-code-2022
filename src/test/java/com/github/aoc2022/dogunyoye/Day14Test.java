package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day14Test {
    private static List<String> walls;
    private static int[] bounds;
    private static int length;
    private static int depth;

    @BeforeClass
    public static void setUp() throws IOException {
        walls = Files.readAllLines(Path.of("src/test/resources/Day14TestInput.txt"));
        bounds = Day14.findMinMax(walls);

        final int maxX = bounds[2];
        final int maxY = bounds[3];
        final int minX = bounds[0];
        length = maxX - minX + 1000;
        depth = maxY + 1;
    }

    @Test
    public void testPartOne() throws IOException {
        final char[][] cave = Day14.createCave(walls, bounds, length, depth);
        assertEquals(24, Day14.findUnitsOfSandAtRestBeforeOverflow(cave, bounds, length, depth));
    }

    @Test
    public void testPartTwo() throws IOException {
        depth += 2;
        final char[][] cave = Day14.createExtendedCave(walls, bounds, length, depth);
        assertEquals(93, Day14.findUnitsOfSandWhenOriginIsAtRest(cave, bounds, length, depth));
    }
}
