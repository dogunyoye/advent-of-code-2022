package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day22 {

    private enum Direction {
        EAST(0),
        SOUTH(1),
        WEST(2),
        NORTH(3);
        
        private int value;

        private Direction(int value) {
            this.value = value;
        }

        private Direction turn(char direction) {
            return switch(direction) {
                case 'L' -> Arrays.stream(Direction.values()).filter((d) -> d.value == Math.floorMod(this.value - 1, 4)).findFirst().get();
                case 'R' -> Arrays.stream(Direction.values()).filter((d) -> d.value == Math.floorMod(this.value + 1, 4)).findFirst().get();
                default -> throw new RuntimeException("invalid direction: " + direction);
            };
        }
    }

    private static class Position {
        private int i;
        private int j;
        private Direction dir;

        private Position(int i, int j, Direction dir) {
            this.i = i;
            this.j = j;
            this.dir = dir;
        }

        @Override
        public String toString() {
            return "Position [i=" + i + ", j=" + j + ", dir=" + dir + "]";
        }
    }

    private static class InstructionSupplier {
        private final String instructions;
        private int currentIdx;

        private InstructionSupplier(String instructions) {
            this.instructions = instructions;
            this.currentIdx = 0;
        }

        private boolean hasNext() {
            return currentIdx < instructions.length();
        }

        private String next() {
            if (currentIdx >= instructions.length()) {
                return null;
            }

            if (Character.isAlphabetic(instructions.charAt(currentIdx))) {
                return Character.toString(instructions.charAt(currentIdx++));
            }

            final StringBuffer sb = new StringBuffer();
            while (currentIdx < instructions.length()) {
                if (!Character.isDigit(instructions.charAt(currentIdx))) {
                    break;
                }
                sb.append(instructions.charAt(currentIdx++));
            }

            return sb.toString();
        }
    }

    private static void printMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    private static int findMaxLength(List<String> data) {
        int maxLength = Integer.MIN_VALUE;
        for (final String line : data) {
            maxLength = Math.max(maxLength, line.length());
        }

        return maxLength;
    }

    // get the min and max index (width) of the map at every level
    private static Map<Integer, int[]> getMapLengthBounds(char[][] map) {
        final Map<Integer, int[]> lengthBounds = new HashMap<>();
        for (int i = 0; i < map.length; i++) {
            final int[] bounds = new int[]{-1, -1};
            lengthBounds.put(i, bounds);
            int currIdx = 0;
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != '@') {
                    if (lengthBounds.get(i)[0] == -1) {
                        lengthBounds.get(i)[0] = j;
                    }
                    currIdx = j;
                }
            }
            lengthBounds.get(i)[1] = currIdx;
        }

        return lengthBounds;
    }

    private static Map<Integer, int[]> getMapDepthBounds(char[][] map) {
        final Map<Integer, int[]> depthBounds = new HashMap<>();
        for (int i = 0; i < map[0].length; i++) {
            final int[] bounds = new int[]{-1, -1};
            depthBounds.put(i, bounds);
            int currIdx = 0;
            for (int j = 0; j < map.length; j++) {
                if (map[j][i] != '@') {
                    if (depthBounds.get(i)[0] == -1) {
                        depthBounds.get(i)[0] = j;
                    }
                    currIdx = j;
                }
            }
            depthBounds.get(i)[1] = currIdx;
        }
        return depthBounds;
    }

    private static char[][] buildMap(List<String> data) {
        final List<String> mapData = data.subList(0, data.size() - 2);
        final int mapDepth = mapData.size();
        final int mapLength = findMaxLength(mapData);
        final char[][] map = new char[mapDepth][mapLength];

        for (int i = 0; i < mapDepth; i++) {
            for (int j = 0; j < mapLength; j++) {
                final String row = mapData.get(i);
                if (j >= row.length() || row.charAt(j) == ' ') {
                    map[i][j] = '@';
                } else {
                    map[i][j] = row.charAt(j);
                }
            }
        }

        return map;
    }

    public static int findPassword(List<String> data) {
        final char[][] map = buildMap(data);
        final InstructionSupplier supplier = new InstructionSupplier(data.get(data.size() - 1));
        final Position pos = new Position(0, data.get(0).indexOf('.'), Direction.EAST);

        final Map<Integer, int[]> lengthBounds = getMapLengthBounds(map);
        final Map<Integer, int[]> depthBounds = getMapDepthBounds(map);

        while (supplier.hasNext()) {
            final String instruction = supplier.next();
            if (instruction.length() == 1 && Character.isLetter(instruction.charAt(0))) {
                pos.dir = pos.dir.turn(instruction.charAt(0));
                continue;
            }

            int steps = Integer.parseInt(instruction);
            final int minBound;
            final int maxBound;
            int ii = pos.i;
            int jj = pos.j;

            switch(pos.dir) {
                case EAST:
                    minBound = lengthBounds.get(pos.i)[0];
                    maxBound = lengthBounds.get(pos.i)[1];
                    while(steps > 0) {
                        ++jj;
                        if (jj == maxBound + 1) {
                            jj = minBound;
                        }

                        if (map[pos.i][jj] != '#') {
                            pos.j = jj;
                            steps--;
                            continue;
                        }
                        break;
                    }
                    break;
                case NORTH:
                    minBound = depthBounds.get(pos.j)[0];
                    maxBound = depthBounds.get(pos.j)[1];
                    while(steps > 0) {
                        --ii;
                        if (ii == minBound - 1) {
                            ii = maxBound;
                        }

                        if (map[ii][pos.j] != '#') {
                            pos.i = ii;
                            steps--;
                            continue;
                        }
                        break;
                    }
                    break;
                case SOUTH:
                    minBound = depthBounds.get(pos.j)[0];
                    maxBound = depthBounds.get(pos.j)[1];
                    while(steps > 0) {
                        ++ii;
                        if (ii == maxBound + 1) {
                            ii = minBound;
                        }

                        if (map[ii][pos.j] != '#') {
                            pos.i = ii;
                            steps--;
                            continue;
                        }
                        break;
                    }
                    break;
                case WEST:
                    minBound = lengthBounds.get(pos.i)[0];
                    maxBound = lengthBounds.get(pos.i)[1];
                    while(steps > 0) {
                        --jj;
                        if (jj == minBound - 1) {
                            jj = maxBound;
                        }

                        if (map[ii][jj] != '#') {
                            pos.j = jj;
                            steps--;
                            continue;
                        }
                        break;
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown direction: " + pos.dir);
            }
        }

        return (1000 * (pos.i + 1)) + (4 * (pos.j + 1)) + pos.dir.value;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day22.txt"));
        System.out.println("Part 1: " + findPassword(data));
    }
}
