package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.aoc2022.dogunyoye.Day10.CPU;

public class Day10Test {
    
    private static List<String> program;
    private static CPU cpu;

    @BeforeClass
    public static void setUp() throws IOException {
        program = Files.readAllLines(Path.of("src/test/resources/Day10TestInput.txt"));
        cpu = new CPU();
        cpu.runProgram(program.iterator());
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(13140, Day10.findSumOfSixSignalStrengths(cpu));
    }

    @Test
    public void testPartTwo() throws IOException {
        // visualisation question
        assertTrue(true);
    }
}
