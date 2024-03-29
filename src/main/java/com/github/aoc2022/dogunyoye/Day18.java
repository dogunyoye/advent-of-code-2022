package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 {

    private static record Position (int x, int y, int z) { }

    static class Cube {
        Position pos;
        int visibleFaces;

        Cube(Position pos) {
            this.pos = pos;
            this.visibleFaces = 6;
        }

        int getVisibleFaces() {
            return this.visibleFaces;
        }

        boolean isAdjacent(Cube other) {
            final Position otherPos = other.pos;
            final int x = this.pos.x();
            final int y = this.pos.y();
            final int z = this.pos.z();

            if (otherPos.equals(new Position(x+1, y, z)) || otherPos.equals(new Position(x-1, y, z)) ||
                otherPos.equals(new Position(x, y+1, z)) || otherPos.equals(new Position(x, y-1, z)) ||
                otherPos.equals(new Position(x, y, z+1)) || otherPos.equals(new Position(x, y, z-1))) {
                return true;
            }

            return false;
        }
    }

    static List<Cube> createCubes(List<String> data) {
        final List<Cube> cubes = new ArrayList<>();
        for (final String line : data) {
            final String[] parts = line.split(",");
            final int x = Integer.parseInt(parts[0]);
            final int y = Integer.parseInt(parts[1]);
            final int z = Integer.parseInt(parts[2]);
            cubes.add(new Cube(new Position(x, y, z)));
        }

        return cubes;
    }

    public static int findSurfaceArea(List<Cube> cubes) {

        for (int i = 0; i < cubes.size() - 1; i++) {
            final Cube c1 = cubes.get(i);
            for (int j = i + 1; j < cubes.size(); j++) {
                final Cube c2 = cubes.get(j);
                if (c1.isAdjacent(c2)) {
                    c1.visibleFaces -= 1;
                    c2.visibleFaces -= 1;
                }
            }
        }

        return cubes.stream().collect(Collectors.summingInt(Cube::getVisibleFaces));
    }

    // 0 - minX
    // 1 - maxX
    // 2 - minY
    // 3 - maxY
    // 4 - minZ
    // 5 - maxZ
    private static List<Position> getNeighbours(Position pos, int[] bounds, int padding) {
        final int x = pos.x();
        final int y = pos.y();
        final int z = pos.z();

        final List<Position> neighbours = new ArrayList<>();

        if ((x - 1) >= (bounds[0] - padding)) {
            neighbours.add(new Position(x-1, y, z));
        }

        if ((x + 1) <= (bounds[1] + padding)) {
            neighbours.add(new Position(x+1, y, z));
        }

        if ((y - 1) >= (bounds[2] - padding)) {
            neighbours.add(new Position(x, y-1, z));
        }

        if ((y + 1) <= (bounds[3] + padding)) {
            neighbours.add(new Position(x, y+1, z));
        }

        if ((z - 1) >= (bounds[4] - padding)) {
            neighbours.add(new Position(x, y, z-1));
        }

        if ((z + 1) <= (bounds[5] + padding)) {
            neighbours.add(new Position(x, y, z+1));
        }

        return neighbours;
    }

    public static int findExteriorSurfaceArea(List<Cube> cubes) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        final Set<Position> cubePositions = new HashSet<>();
        final Set<Position> visited = new HashSet<>();

        for (final Cube cube : cubes) {
            final int x = cube.pos.x();
            final int y = cube.pos.y();
            final int z = cube.pos.z();

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);

            final Position p = new Position(x, y, z);
            cubePositions.add(p);
        }

        final int[] bounds = new int[]{minX, maxX, minY, maxY, minZ, maxZ};

        final Position start = new Position(0, 0, 0);
        final Queue<Position> q = new ArrayDeque<>();

        q.add(start);

        int result = 0;

        while (!q.isEmpty()) {
            final Position pos = q.poll();
            if (!visited.contains(pos)) {
                visited.add(pos);
                final List<Position> neighbours = getNeighbours(pos, bounds, 2);
                for (final Position n : neighbours) {
                    if (cubePositions.contains(n)) {
                        ++result;
                    } else {
                        q.add(n);
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day18.txt"));

        System.out.println("Part 1: " + findSurfaceArea(createCubes(data)));
        System.out.println("Part 2: " + findExteriorSurfaceArea(createCubes(data)));
    }
}
