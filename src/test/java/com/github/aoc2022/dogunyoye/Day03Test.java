package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day03Test {

    private static List<String> rucksacks;
    
    @BeforeClass
    public static void setUp() throws IOException {
        rucksacks = Files.readAllLines(Path.of("src/test/resources/Day03TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(157, Day03.calculateRucksackPriorities(rucksacks));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(70, Day03.calculateRucksackBadgePriorities(rucksacks));
    }
}
