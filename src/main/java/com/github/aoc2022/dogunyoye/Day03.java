package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day03 {

    private static Map<Character, Integer> priorities;

    static {
        priorities = new HashMap<>();

        int s = 1;
        for (int i = 'a'; i <= 'z'; i++) {
            priorities.put((char) i, s);
            s++;
        }

        for (int i = 'A'; i <= 'Z'; i++) {
            priorities.put((char) i, s);
            s++;
        }
    }

    private static Set<Character> createItemSet(String item) {
        final Character[] charArr = item.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
        return Arrays.stream(charArr).collect(Collectors.toSet());
    }

    private static record Rucksack (String items) {

        private char findDup() {
            final int length = items.length();

            final String firstCompartment = items.substring(0, length/2);
            final String secondCompartment = items.substring(length/2, length);

            final Set<Character> s = createItemSet(firstCompartment);

            for (char c : secondCompartment.toCharArray()) {
                if (s.contains(c)) {
                    return c;
                }
            }

            throw new RuntimeException("Duplicate item not found");
        }
    }

    public static int calculateRucksackPriorities(List<String> rucksacks) {

        final List<Character> dups = new ArrayList<>();
        for (final String rucksackItems : rucksacks) {
            final Rucksack r = new Rucksack(rucksackItems);
            dups.add(r.findDup());
        }

        int score = 0;
        for (Character d : dups) {
            score += priorities.get(d);
        }

        return score;
    }

    public static int calculateRucksackBadgePriorities(List<String> rucksacks) {

        Set<Character> s1;
        Set<Character> s2;
        Set<Character> s3;

        final List<Character> badges = new ArrayList<>();
        
        for (int i = 0; i < rucksacks.size(); i+=3) {
            s1 = createItemSet(rucksacks.get(i));
            s2 = createItemSet(rucksacks.get(i+1));
            s3 = createItemSet(rucksacks.get(i+2));

            if(s1.retainAll(s2) && s1.retainAll(s3)) {
                badges.add(s1.stream().findFirst().get());
            }
        }

        int score = 0;
        for (final char b : badges) {
            score += priorities.get(b);
        }

        return score;
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> rucksacks = Files.readAllLines(Path.of("src/main/resources/Day03.txt"));

        System.out.println("Part 1: " + calculateRucksackPriorities(rucksacks));
        System.out.println("Part 2: " + calculateRucksackBadgePriorities(rucksacks));
    }
}
