package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day22 {

    private enum Direction {
        EAST(0),
        SOUTH(1),
        WEST(2),
        NORTH(3);
        
        private int value;

        Direction(int value) {
            this.value = value;
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
    }

    private static class InstructionSupplier {
        private final String instructions;
        private int currentIdx;

        private InstructionSupplier(String instructions) {
            this.instructions = instructions;
            this.currentIdx = 0;
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
        String instruction = supplier.next();

        while (instruction != null) {
            System.out.println(instruction);
            instruction = supplier.next();
        }

        return 0;
    }

    
    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day22.txt"));
        System.out.println("Part 1: " + findPassword(data));
    }
}
