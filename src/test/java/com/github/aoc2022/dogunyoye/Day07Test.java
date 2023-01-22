package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day07Test {

    private static List<String> output;

    @BeforeClass
    public static void setUp() throws IOException {
        output = Files.readAllLines(Path.of("src/test/resources/Day07TestInput.txt"));
        Day07.traverseFileSystem(output);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(95437, Day07.findSumOfDirectoriesLessThan100000());
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(24933642, Day07.findSmallestDirectoryToDelete());
    }
}
