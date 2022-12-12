package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day12Test {

    private static int[][] heightMap;
    private static int length;
    private static int depth;

    @BeforeClass
    public static void setUp() throws IOException {
        final List<String> map = Files.readAllLines(Path.of("src/test/resources/Day12TestInput.txt"));
        length = map.get(0).length();
        depth = map.size();
        heightMap = Day12.buildHeightMap(map, length, depth);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(31, Day12.fewestStepsToBestSignalLocation(heightMap, length, depth));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(29, Day12.fewestStepsFromAtoBestSignalLocation(heightMap, length, depth));
    }
}
