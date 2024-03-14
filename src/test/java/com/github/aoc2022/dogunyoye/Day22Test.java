package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day22Test {

    private static List<String> data;

    @BeforeClass
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day22TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(6032, Day22.findPassword(data));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(5031, Day22.findPasswordForAlteredMapTraversal(data, true));
    }
}
