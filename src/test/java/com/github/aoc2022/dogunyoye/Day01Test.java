package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.aoc2022.dogunyoye.Day01.Elf;

public class Day01Test {

    private static List<Elf> elfCalories;

    @BeforeClass
    public static void setUp() throws IOException {
        final List<String> allCalories = Files.readAllLines(Path.of("src/test/resources/Day01TestInput.txt"));
        elfCalories = Day01.getElfCalories(allCalories);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(24000, Day01.findMaxCalories(elfCalories));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(45000, Day01.findTopThreeCalories(elfCalories));
    }
}