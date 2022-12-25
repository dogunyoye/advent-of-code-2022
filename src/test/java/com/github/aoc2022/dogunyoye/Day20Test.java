package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day20Test {

    private static List<String> data;

    @BeforeClass
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day20TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(3, Day20.findThreeNumberSum(Day20.createNodes(data), 1));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(1623178306, Day20.findThreeNumberSumWithDecryptionKeyApplied(Day20.createNodes(data), 10));
    }
}
