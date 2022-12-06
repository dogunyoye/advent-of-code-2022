package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.Test;

public class Day06Test {

    private static String dataStream;

    @BeforeClass
    public static void setUp() throws IOException {
        dataStream = Files.readAllLines(Path.of("src/test/resources/Day06TestInput.txt")).get(0);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(7, Day06.numberOfCharactersBeforeStartOfPacket(dataStream));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(19, Day06.numberOfCharactersBeforeStartOfMessage(dataStream));
    }
}
