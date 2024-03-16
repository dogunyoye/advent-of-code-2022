package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day23Test {

    private static List<String> data;

    @BeforeClass
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day23TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(110, Day23.findEmptyGroundTilesAfter10Rounds(data));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(20, Day23.findRoundWhereNoElvesMove(data));
    }
}
