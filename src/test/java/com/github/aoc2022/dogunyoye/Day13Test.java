package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;

public class Day13Test {
    
    private static List<String> packetData;
    private static List<JSONArray> orderedPackets;

    @BeforeClass
    public static void setUp() throws IOException {
        packetData = Files.readAllLines(Path.of("src/test/resources/Day13TestInput.txt"));
        orderedPackets = new ArrayList<>();
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(13, Day13.parsePacketsAsJson(packetData, orderedPackets));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(140, Day13.calculateDecoderKey(orderedPackets));
    }
}
