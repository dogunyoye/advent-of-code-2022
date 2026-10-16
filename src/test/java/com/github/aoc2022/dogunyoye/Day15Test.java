package com.github.aoc2022.dogunyoye;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.aoc2022.dogunyoye.Day15.Sensor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Day15Test {
    private static List<String> data;
    private static List<Sensor> sensors;

    @BeforeAll
    public static void setUp() throws IOException {
        data = Files.readAllLines(Path.of("src/test/resources/Day15TestInput.txt"));
        sensors = Day15.parseSensors(data);
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals(26, Day15.getNumberOfPositionsWithNoBeacon(sensors, 10));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals(56000011, Day15.calculateTuningFrequency(sensors, 20));
    }
}
