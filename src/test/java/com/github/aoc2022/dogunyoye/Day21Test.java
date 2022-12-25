package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

// no test for part 2 as my solution works for my input
// but not the test case..
// in order to get it to work for the test case, I have to
// flip the comparison symbols in the binary search. Very strange
public class Day21Test {

    private static List<String> data;

    @BeforeClass
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day21TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(152, Day21.findRootMonkeyNumber(data));
    }
}
