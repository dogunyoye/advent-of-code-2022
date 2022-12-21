package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day16 {

    private static Map<Valve, List<String>> valvesMap;
    private static Map<String, Valve> nameToValveMap;
    private static Map<State, Integer> memo;

    static class State {
        String valveName;
        Set<Valve> visited;
        int timeLeft;
        int numberOfPlayers;

        State(String name, Set<Valve> visited, int timeLeft, int numberOfPlayers) {
            this.valveName = name;
            this.visited = visited;
            this.timeLeft = timeLeft;
            this.numberOfPlayers = numberOfPlayers;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((valveName == null) ? 0 : valveName.hashCode());
            result = prime * result + ((visited == null) ? 0 : visited.hashCode());
            result = prime * result + timeLeft;
            result = prime * result + numberOfPlayers;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            State other = (State) obj;
            if (valveName == null) {
                if (other.valveName != null)
                    return false;
            } else if (!valveName.equals(other.valveName))
                return false;
            if (visited == null) {
                if (other.visited != null)
                    return false;
            } else if (!visited.equals(other.visited))
                return false;
            if (timeLeft != other.timeLeft)
                return false;
            if (numberOfPlayers != other.numberOfPlayers)
                return false;
            return true;
        }
    }

    static class Valve {
        String name;
        int flowRate;

        Valve(String name, int flowRate) {
            this.name = name;
            this.flowRate = flowRate;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + flowRate;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Valve other = (Valve) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (flowRate != other.flowRate)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Valve:[" + this.name + ", " + this.flowRate + "]";
        }
    }

    static Map<Valve, List<String>> parseValves(List<String> valves) {
        valvesMap = new HashMap<>();
        nameToValveMap = new HashMap<>();

        for (String valve : valves) {
            valve = valve.replaceAll(",", "")
                .replaceAll(";", "")
                .replaceAll("rate=", "");
                
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

        return valvesMap;
    }

    private static int traverseValves(String currentValve, Set<Valve> visited, int timeLeft, int numberOfPlayers) {
        if (timeLeft == 0) {
            if (numberOfPlayers == 2) {
                return traverseValves("AA", visited, 26, numberOfPlayers - 1);
            }
            return 0;
        }

        final State state = new State(currentValve, visited, timeLeft, numberOfPlayers);
        final Integer cached = memo.get(state);
        if (cached != null && cached >= 0) {
            return cached;
        }

        int result = 0;
        final Valve valve = nameToValveMap.get(currentValve);
        if (!visited.contains(valve) && valve.flowRate > 0) {
            final Set<Valve> newVisited = new HashSet<>(visited);
            newVisited.add(valve);
            result = Math.max(result, ((timeLeft - 1) * valve.flowRate) + traverseValves(valve.name, newVisited, timeLeft - 1, numberOfPlayers));
        }

        for (final String child : valvesMap.get(valve)) {
            result = Math.max(result, traverseValves(child, visited, timeLeft - 1, numberOfPlayers));
        }

        memo.put(state, result);
        return result;
    }

    public static int findMaximumPressureIn30Mins() {
        memo = new HashMap<>();
        return traverseValves("AA", new HashSet<>(), 30, 1);
    }

    // takes a long time to complete
    // TODO: Optimise
    public static int findMaximumPressureWithElephant() {
        memo = new HashMap<>();
        return traverseValves("AA", new HashSet<>(), 26, 2);
    }

    public static void main(String[] args) throws IOException {
        final List<String> valves = Files.readAllLines(Path.of("src/main/resources/Day16.txt"));
        parseValves(valves);

        System.out.println("Part 1: " + findMaximumPressureIn30Mins());
        System.out.println("Part 2: " + findMaximumPressureWithElephant());
    }
}
