package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day01 {

    public static class Elf {
        private int calories;

        public Elf() {
            this.calories = 0;
        }

        public void addCalories(int calories) {
            this.calories += calories;
        }

        public int getCalories() {
            return this.calories;
        }
    }

    static List<Elf> getElfCalories(List<String> allCalories) {
        final List<Elf> elves = new ArrayList<>();

        Elf e = new Elf();
        for (String line : allCalories) {
            if (line.isEmpty()) {
                elves.add(e);
                e = new Elf();
                continue;
            }

            e.addCalories(Integer.parseInt(line));
        }

        // add the last elf
        elves.add(e);

        elves.sort(Comparator.comparing(Elf::getCalories).reversed());
        return elves;
    }

    public static int findMaxCalories(List<Elf> elves) {
        return elves.get(0).getCalories();
    }

    public static int sumTopThreeCalories(List<Elf> elves) {
        return elves.stream().limit(3).map(Elf::getCalories).collect(Collectors.summingInt(Integer::intValue));
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> allCalories = Files.readAllLines(Path.of("src/main/resources/Day01.txt"));
        final List<Elf> elfCalories = getElfCalories(allCalories);

        System.out.println("Part 1: " + findMaxCalories(elfCalories));
        System.out.println("Part 2: " + sumTopThreeCalories(elfCalories));
    }
}
