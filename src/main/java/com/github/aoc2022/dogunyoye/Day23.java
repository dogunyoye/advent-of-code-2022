package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class Day23 {

    private enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST
    }

    private static class Position {
        private final int i;
        private final int j;

        private Position(int i, int j) {
            this.i = i;
            this.j = j;
        }

        private List<Character> neighbours(Map<Position, Character> map) {
            final List<Character> neighbours = new ArrayList<>();

            //NW
            neighbours.add(getValue(i-1, j-1, map));
            //N
            neighbours.add(getValue(i-1, j, map));
            //NE
            neighbours.add(getValue(i-1, j+1, map));
            //E
            neighbours.add(getValue(i, j+1, map));
            //SE
            neighbours.add(getValue(i+1, j+1, map));
            //S
            neighbours.add(getValue(i+1, j, map));
            //SW
            neighbours.add(getValue(i+1, j-1, map));
            //W
            neighbours.add(getValue(i, j-1, map));

            return neighbours;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + i;
            result = prime * result + j;
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
            Position other = (Position) obj;
            if (i != other.i)
                return false;
            if (j != other.j)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Position [i=" + i + ", j=" + j + "]";
        }
    }

    private static Character getValue(int i, int j, Map<Position, Character> map) {
        final Character value = map.get(new Position(i, j));
        if (value == null) {
            map.put(new Position(i, j), '.');
            return '.';
        }

        return value;
    }

    private static List<Position> findElves(Map<Position, Character> map) {
        return new ArrayList<>(map.entrySet().stream().filter((e) -> e.getValue() == '#').map((e) -> e.getKey()).toList());
    }

    private static Map<Position, Character> buildMap(List<String> data) {
        final Map<Position, Character> map = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(0).length(); j++) {
                map.put(new Position(i, j), data.get(i).charAt(j));
            }
        }

        return map;
    }

    public static int simulateElfMovement(List<String> data, boolean isPartOne) {
        final Map<Position, Character> map = buildMap(data);
        final List<Direction> order = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
        int rounds = 0;

        while (true) {

            if (isPartOne && rounds == 10) {
                break;
            }

            final List<Position> elves = findElves(map);
            final Map<Position, Position> moveMap = new HashMap<>();

            for (final Position elf : elves) {
                final List<Character> neighbours = elf.neighbours(map);
                if (neighbours.stream().allMatch((c) -> c == null || c == '.')) {
                    continue;
                }

                for (final Direction dir : order) {
                    final Stream<Character> stream;
                    final Position proposedPos;
                    switch (dir) {
                        case EAST:
                            // 2, 3, 4
                            stream = Arrays.asList(neighbours.get(2), neighbours.get(3), neighbours.get(4)).stream();
                            proposedPos = new Position(elf.i, elf.j + 1);
                            break;
                        case NORTH:
                            // 0, 1, 2
                            stream = Arrays.asList(neighbours.get(0), neighbours.get(1), neighbours.get(2)).stream();
                            proposedPos = new Position(elf.i - 1, elf.j);
                            break;
                        case SOUTH:
                            // 4, 5, 6
                            stream = Arrays.asList(neighbours.get(4), neighbours.get(5), neighbours.get(6)).stream();
                            proposedPos = new Position(elf.i + 1, elf.j);
                            break;
                        case WEST:
                            // 0, 6, 7
                            stream = Arrays.asList(neighbours.get(0), neighbours.get(6), neighbours.get(7)).stream();
                            proposedPos = new Position(elf.i, elf.j - 1);
                            break;
                        default:
                            throw new RuntimeException("Unknown direction: " + dir);
                    }

                    if (stream.allMatch((c) -> c == null || c == '.')) {
                        moveMap.put(elf, proposedPos);
                        break;
                    }
                }
            }

            if (!isPartOne && moveMap.isEmpty()) {
                return rounds + 1;
            }

            for (final Entry<Position, Position> e : moveMap.entrySet()) {
                if (Collections.frequency(moveMap.values(), e.getValue()) == 1) {
                    final Position current = e.getKey();
                    final Position newPos = e.getValue();
                    map.put(new Position(current.i, current.j), '.');
                    map.put(new Position(newPos.i, newPos.j), '#');
                }
            }

            order.add(order.get(0));
            order.remove(0);

            ++rounds;
        }

        final List<Position> elves = findElves(map);

        elves.sort((p1, p2) -> Integer.compare(p1.i, p2.i));
        final int[] minMaxDepth = new int[]{elves.get(0).i, elves.get(elves.size() - 1).i};

        elves.sort((p1, p2) -> Integer.compare(p1.j, p2.j));
        final int[] minMaxLength = new int[]{elves.get(0).j, elves.get(elves.size() - 1).j};

        int result = 0;
        for (int i = minMaxDepth[0]; i <= minMaxDepth[1]; i++) {
            for (int j = minMaxLength[0]; j <= minMaxLength[1]; j++) {
                final Character value = map.get(new Position(i, j));
                if (value == null || value == '.') {
                    ++result;
                }
            }
        }

        return result;
    }

    public static int findEmptyGroundTilesAfter10Rounds(List<String> data) {
        return simulateElfMovement(data, true);
    }

    public static int findRoundWhereNoElvesMove(List<String> data) {
        return simulateElfMovement(data, false);
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day23.txt"));
        System.out.println("Part 1: " + findEmptyGroundTilesAfter10Rounds(data));
        System.out.println("Part 2: " + findRoundWhereNoElvesMove(data));
    }
}
