package com.github.aoc2022.dogunyoye;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.aoc2022.dogunyoye.Day04.ElfPair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Day04Test {
    private static List<ElfPair> elfPairs;

    @BeforeAll
    public static void setUp() throws IOException {
        elfPairs = Day04.createElfPairs(Files.readAllLines(Path.of("src/test/resources/Day04TestInput.txt")));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(2, Day04.findNumberOfOverlaps(elfPairs));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(4, Day04.findNumberOfPartialOverlaps(elfPairs));
    }
}
