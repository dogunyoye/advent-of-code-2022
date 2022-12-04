package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day04 {

    static class ElfPair {
        private Set<Integer> e1Sections;
        private Set<Integer> e2Sections;

        private ElfPair(int e1Start, int e1End, int e2Start, int e2End) {
            this.e1Sections = new HashSet<>();
            this.e2Sections = new HashSet<>();

            for (int i = e1Start; i <= e1End; i++) {
                e1Sections.add(i);
            }

            for (int i = e2Start; i <= e2End; i++) {
                e2Sections.add(i);
            }
        }

        private boolean elvesCompletelyOverlap() {
            return e1Sections.containsAll(e2Sections) || e2Sections.containsAll(e1Sections);
        }

        private boolean elvesPartiallyOverlap() {
            final Set<Integer> e1 = new HashSet<>(e1Sections);
            final Set<Integer> e2 = new HashSet<>(e2Sections);
            e1.retainAll(e2);

            return !e1.isEmpty();
        }
    }

    public static int findNumberOfOverlaps(List<ElfPair> elfPairs) {
        int overlaps = 0;

        for (final ElfPair ep : elfPairs) {
            if (ep.elvesCompletelyOverlap()) {
                overlaps++;
            }
        }

        return overlaps;
    }

    public static int findNumberOfPartialOverlaps(List<ElfPair> elfPairs) {
        int overlaps = 0;

        for (final ElfPair ep : elfPairs) {
            if (ep.elvesPartiallyOverlap()) {
                overlaps++;
            }
        }

        return overlaps;
    }

    static List<ElfPair> createElfPairs(List<String> elfPairs) {

        final List<ElfPair> eps = new ArrayList<>();
        for (final String pair : elfPairs) {
            final String[] sections = pair.split(",");
            final String[] e1Sections = sections[0].split("-");
            final String[] e2Sections = sections[1].split("-");
            final ElfPair ep = new ElfPair(
                Integer.parseInt(e1Sections[0]), Integer.parseInt(e1Sections[1]),
                Integer.parseInt(e2Sections[0]), Integer.parseInt(e2Sections[1]));
            eps.add(ep);
        }

        return eps;
    }
    
    public static void main(String[] args) throws IOException {
        final List<ElfPair> eps = createElfPairs(Files.readAllLines(Path.of("src/main/resources/Day04.txt")));

        System.out.println("Part 1: " + findNumberOfOverlaps(eps));
        System.out.println("Part 2: " + findNumberOfPartialOverlaps(eps));
    }
}
