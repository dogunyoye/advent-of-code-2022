package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.aoc2022.dogunyoye.Day08.Tree;

public class Day08Test {
    private static List<String> grid;
    private static int length;
    private static int depth;

    private static Tree[][] map;

    @BeforeClass
    public static void setUp() throws IOException {
        grid = Files.readAllLines(Path.of("src/test/resources/Day08TestInput.txt"));
        length = grid.get(0).length();
        depth = grid.size();

        map = Day08.buildMap(grid, length, depth);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(21, Day08.findNumberOfVisibleTrees(map, length, depth));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(8, Day08.findBestScenicScore(map, length, depth));
    }
}
