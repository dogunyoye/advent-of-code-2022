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

        private Direction turnLeft() {
            return Arrays.stream(Direction.values()).filter((d) -> d.value == Math.floorMod(this.value - 1, 4)).findFirst().get();
        }

        private Direction turnRight() {
            return Arrays.stream(Direction.values()).filter((d) -> d.value == Math.floorMod(this.value + 1, 4)).findFirst().get();
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
        printMap(map);

        final Map<Integer, int[]> lengthBounds = getMapLengthBounds(map);
        lengthBounds.forEach((k, v) -> {
            System.out.println(k + " " + ": " + Arrays.toString(v));
        });

        System.out.println("====================");

        final Map<Integer, int[]> depthBounds = getMapDepthBounds(map);
        depthBounds.forEach((k, v) -> {
            System.out.println(k + " " + ": " + Arrays.toString(v));
        });

        while (supplier.hasNext()) {
            final String instruction = supplier.next();
            //System.out.println(instruction);
        }

        System.out.println(Direction.EAST.turnLeft());
        System.out.println(Direction.EAST.turnRight());

        return 0;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day22.txt"));
        System.out.println("Part 1: " + findPassword(data));
    }
}
