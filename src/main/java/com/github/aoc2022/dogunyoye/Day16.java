package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Day16 {

    private static Map<Valve, List<String>> valvesMap;
    private static Map<String, Valve> nameToValveMap;
    private static Map<SearchState, Integer> memo;
    private static Map<String, Map<String, Integer>> distances;
    private static List<Valve> workingValves;
    private static Map<String, Integer> valveBitIndex;

    private static record SearchState(String valveName, long openedValves, int timeLeft, int numberOfPlayers) {
    }

    private static record Valve(String name, int flowRate) {
    }

    static Map<Valve, List<String>> parseValves(List<String> valves) {
        valvesMap = new HashMap<>();
        nameToValveMap = new HashMap<>();

        for (String valve : valves) {
            valve = valve.replaceAll(",", "").replaceAll(";", "").replaceAll("rate=", "");

            final String[] parts = valve.split(" ");
            final String name = parts[1];
            final int flowRate = Integer.parseInt(parts[4]);

            final List<String> children = new ArrayList<>();
            for (int i = 9; i < parts.length; i++) {
                children.add(parts[i]);
            }

            final Valve v = new Valve(name, flowRate);
            valvesMap.put(v, children);
            nameToValveMap.put(name, v);
        }

        computeShortestPaths();

        return valvesMap;
    }

    private static void computeShortestPaths() {
        distances = new HashMap<>();
        workingValves = new ArrayList<>();
        valveBitIndex = new HashMap<>();

        for (final Valve valve : valvesMap.keySet()) {
            if (valve.flowRate() > 0) {
                valveBitIndex.put(valve.name(), workingValves.size());
                workingValves.add(valve);
            }
        }

        final List<String> origins = new ArrayList<>();
        origins.add("AA");
        for (final Valve valve : workingValves) {
            origins.add(valve.name());
        }

        for (final String origin : origins) {
            distances.put(origin, bfsDistances(origin));
        }
    }

    private static Map<String, Integer> bfsDistances(String origin) {
        final Map<String, Integer> dist = new HashMap<>();
        final Queue<String> queue = new ArrayDeque<>();
        final Set<String> seen = new HashSet<>();
        queue.add(origin);
        seen.add(origin);
        dist.put(origin, 0);

        while (!queue.isEmpty()) {
            final String current = queue.poll();
            for (final String child : valvesMap.get(nameToValveMap.get(current))) {
                if (seen.add(child)) {
                    dist.put(child, dist.get(current) + 1);
                    queue.add(child);
                }
            }
        }

        return dist;
    }

    private static int maxPressure(String currentValve, int timeLeft, long openedValves, int numberOfPlayers) {
        if (timeLeft <= 0) {
            if (numberOfPlayers == 2) {
                return maxPressure("AA", 26, openedValves, numberOfPlayers - 1);
            }
            return 0;
        }

        final SearchState state = new SearchState(currentValve, openedValves, timeLeft, numberOfPlayers);
        final Integer cached = memo.get(state);
        if (cached != null) {
            return cached;
        }

        int best = 0;
        if (numberOfPlayers == 2) {
            // jumps can skip past timeLeft == 0, so player 1 may stop at any
            // point and hand the remaining valves over to player 2
            best = maxPressure("AA", 26, openedValves, numberOfPlayers - 1);
        }
        for (final Valve valve : workingValves) {
            final long bit = 1L << valveBitIndex.get(valve.name());
            if ((openedValves & bit) != 0) {
                continue;
            }
            final int cost = distances.get(currentValve).get(valve.name()) + 1;
            if (timeLeft > cost) {
                final int gain = (timeLeft - cost) * valve.flowRate();
                best = Math.max(
                        best, gain + maxPressure(valve.name(), timeLeft - cost, openedValves | bit, numberOfPlayers));
            }
        }

        memo.put(state, best);
        return best;
    }

    public static int findMaximumPressureIn30Mins() {
        memo = new HashMap<>();
        return maxPressure("AA", 30, 0L, 1);
    }

    public static int findMaximumPressureWithElephant() {
        memo = new HashMap<>();
        return maxPressure("AA", 26, 0L, 2);
    }

    public static void main(String[] args) throws IOException {
        final List<String> valves = Files.readAllLines(Path.of("src/main/resources/Day16.txt"));
        parseValves(valves);

        System.out.println("Part 1: " + findMaximumPressureIn30Mins());
        System.out.println("Part 2: " + findMaximumPressureWithElephant());
    }
}
