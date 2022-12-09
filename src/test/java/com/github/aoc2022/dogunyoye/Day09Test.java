package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day09Test {

    private static List<String> instructions;

    @BeforeClass
    public static void setUp() throws IOException {
        instructions = Files.readAllLines(Path.of("src/test/resources/Day09TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(13, Day09.numberOfStepsInTailPath(instructions.iterator()));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(1, Day09.numberOfStepsInTailPathWith10Knots(instructions.iterator()));
    }
}
