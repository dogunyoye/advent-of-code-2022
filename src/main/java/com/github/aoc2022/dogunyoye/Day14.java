package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day14 {

    static class Point {
        int x;
        int y;
        boolean outOfBounds = false;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Point(int x, int y, boolean outOfBounds) {
            this(x, y);
            this.outOfBounds = outOfBounds;
        }

        @Override
        public String toString() {
            return "Point[X:" + this.x + ", Y:" + this.y + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + (outOfBounds ? 1231 : 1237);
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
            Point other = (Point) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (outOfBounds != other.outOfBounds)
                return false;
            return true;
        }
    }

    private static int[] findMinMax(List<String> walls) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (final String line : walls) {
            final String[] parts = line.split(" -> ");
            for (final String coordinate : parts) {
                final String[] commaSplit = coordinate.split(",");
                final int x = Integer.parseInt(commaSplit[0]);
                final int y = Integer.parseInt(commaSplit[1]);

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        return new int[]{minX, minY, maxX, maxY};

    }

    static char[][] createWalls(List<String> walls, int[] bounds, int length, int depth) {
        final int minX = bounds[0];

        final char[][] wallMap = new char[depth][length+1];

        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < length+1; j++) {
                wallMap[i][j] = '.';
            }
        }

        for (final String line : walls) {
            final String[] parts = line.split(" -> ");
            for (int i = 0; i < parts.length-1; i++) {
                final String[] p1 = parts[i].split(",");
                final String[] p2 = parts[i+1].split(",");

                final int p1X = Integer.parseInt(p1[0]);
                final int p1Y = Integer.parseInt(p1[1]);

                final int p2X = Integer.parseInt(p2[0]);
                final int p2Y = Integer.parseInt(p2[1]);

                final int dx = p1X - p2X;
                final int dy = p1Y - p2Y;

                int max = 0;
                int min = 0;

                if (dx == 0) {
                    max = Math.max(p1Y, p2Y);
                    min = Math.min(p1Y, p2Y);

                    for (int col = min; col <= max; col++) {
                        wallMap[col][p1X - minX + 250] = '#';
                    }
                } else if (dy == 0) {
                    max = Math.max(p1X, p2X);
                    min = Math.min(p1X, p2X);

                    for (int row = min; row <= max; row++) {
                        wallMap[p1Y][row - minX + 250] = '#';
                    }
                }
            }
        }

        return wallMap;
    }

    private static Point checkLeft(char[][] wallMap, int depth, Point location) {
        int x = location.x-1;
        int y = location.y+1;

        // past the floor
        if (y >= depth) {
            return new Point(x, y, true);
        }

        if (wallMap[y][x] == '.') {
            return new Point(x, y);
        }

        // occupied by sand
        return new Point(x, y, true);
    }

    private static Point checkRight(char[][] wallMap, int depth, Point location) {
        int x = location.x+1;
        int y = location.y+1;

        // past the floor
        if (y >= depth) {
            return new Point(x, y, true);
        }

        if (wallMap[y][x] == '.') {
            return new Point(x, y);
        }

        // occupied by sand
        return new Point(x, y, true);
    }

    private static Point drop(char[][] wallMap, int depth, Point dropPoint) {

        int x = dropPoint.x;
        int y = dropPoint.y;

        Point rest;

        while (wallMap[y][x] != 'o' && wallMap[y][x] != '#') {
            // fall
            ++y;

            // fallen out of bounds
            if (y >= depth) {
                return new Point(x, y, true);
            }
        }

        // go back up
        --y;

        rest = new Point(x, y);

        // check if I can go left
        // if so, recurse left
        final Point left = checkLeft(wallMap, depth, rest);
        if (!left.outOfBounds) {
            rest = drop(wallMap, depth, left);
        }

        // check if I can go right
        // if so, recurse right
        final Point right = checkRight(wallMap, depth, rest);
        if (!right.outOfBounds) {
            rest = drop(wallMap, depth, right);
        }

        return rest;
    }

    public static int findUnitsOfSandAtRestBeforeOverflow(char[][] wallMap, int[] bounds, int length, int depth) {
        final int x = 500-bounds[0] + 250;
        final Point dropPoint = new Point(x, 0);

        int count = 0;
        while (true) {
            final Point restPoint = drop(wallMap, depth, dropPoint);
            // as soon as we've returned an out of bounds
            // rest point, we've overflown
            if (restPoint.outOfBounds) {
                printMap(wallMap, depth, length);
                break;
            }
            wallMap[restPoint.y][restPoint.x] = 'o';
            count++;
        }

        return count;
    }

    private static void printMap(char[][] map, int depth, int length) {
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public static int findUnitsOfSandWhenOriginIsAtRest(char[][] extendedWallMap, int[] bounds, int length, int depth) {
        final int x = 500-bounds[0] + 250;
        final Point dropPoint = new Point(x, 0);

        int count = 0;
        while (true) {
            Point restPoint = drop(extendedWallMap, depth, dropPoint);
            // when we return a rest point which is
            // the same as the original drop point,
            // get out.
            if (restPoint.x == x && restPoint.y == 0) {
                //printMap(extendedWallMap, depth, length);
                break;
            }
            extendedWallMap[restPoint.y][restPoint.x] = 'o';
            count++;
        }

        return count + 1;
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> walls = Files.readAllLines(Path.of("src/main/resources/Day14.txt"));
        final int[] bounds = findMinMax(walls);

        final int maxX = bounds[2];
        final int maxY = bounds[3];
        final int minX = bounds[0];
        final int length = maxX - minX + 500;
        int depth = maxY + 1;
    
        final char[][] wallmap = createWalls(walls, bounds, length, depth);
        System.out.println("Part 1: " + findUnitsOfSandAtRestBeforeOverflow(wallmap, bounds, length, depth));

        // create floor, 2 levels down
        depth += 2;
        final char[][] extendedWallMap = createWalls(walls, bounds, length, depth);
        for (int i = 0; i < length; i++) {
            extendedWallMap[depth-1][i] = '#';
        }

        System.out.println("Part 2: " + findUnitsOfSandWhenOriginIsAtRest(extendedWallMap, bounds, length, depth));
    }
}
