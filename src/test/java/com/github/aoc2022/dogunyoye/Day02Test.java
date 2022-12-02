package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.Day02;

public class Day02Test {

    private static List<String> game;
    
    @BeforeClass
    public static void setUp() throws IOException {
        game = Files.readAllLines(Path.of("src/test/resources/Day02TestInput.txt"));
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(15, Day02.calculateScore(game));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(12, Day02.calculateNewStrategyScore(game));
    }
}
