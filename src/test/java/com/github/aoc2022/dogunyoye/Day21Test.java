package com.github.aoc2022.dogunyoye;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Day21Test {

    private static List<String> data;

    @BeforeAll
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day21TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(152, Day21.findRootMonkeyNumber(data));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(301, Day21.findNumberToPassRootEqualityCheck(data));
    }
}
