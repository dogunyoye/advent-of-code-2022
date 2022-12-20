package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day16 {

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
        final Map<Valve, List<String>> valvesMap = new HashMap<>();

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

            valvesMap.put(new Valve(name, flowRate), children);
        }

        System.out.println(valvesMap);
        return valvesMap;
    }

    public static void main(String[] args) throws IOException {
        final List<String> valves = Files.readAllLines(Path.of("src/main/resources/Day16.txt"));
        parseValves(valves);
    }
}
