package com.github.aoc2022.dogunyoye;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.aoc2022.dogunyoye.Day05.Cargo;
import com.github.aoc2022.dogunyoye.Day05.CrateMover9000;
import com.github.aoc2022.dogunyoye.Day05.CrateMover9001;
import com.github.aoc2022.dogunyoye.Day05.Instruction;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Day05Test {
    private static List<Cargo> cargos;
    private static List<Instruction> instructions;
    
    @Before
    public void setUp() throws IOException {
        List[] cargosAndInstructions = Day05.parseCargoAndInstructions(Files.readAllLines(Path.of("src/test/resources/Day05TestInput.txt")), 3);
        cargos = cargosAndInstructions[0];
        instructions = cargosAndInstructions[1];
    }

    @Test
    public void testPartOne() throws IOException {
        assertEquals("CMZ", Day05.findTopItems(new CrateMover9000(), cargos, instructions));
    }

    @Test
    public void testPartTwo() throws IOException {
        assertEquals("MCD", Day05.findTopItems(new CrateMover9001(), cargos, instructions));
    }
}
