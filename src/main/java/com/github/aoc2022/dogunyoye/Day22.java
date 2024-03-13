package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static class CubeFace {
        private final int id;
        private final Set<Position> points;
        private final List<List<Position>> borders;

        private CubeFace(int id, Set<Position> points, List<List<Position>> borders) {
            this.id = id;
            this.points = points;
            this.borders = borders;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ((points == null) ? 0 : points.hashCode());
            result = prime * result + ((borders == null) ? 0 : borders.hashCode());
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
            CubeFace other = (CubeFace) obj;
            if (id != other.id)
                return false;
            if (points == null) {
                if (other.points != null)
                    return false;
            } else if (!points.equals(other.points))
                return false;
            if (borders == null) {
                if (other.borders != null)
                    return false;
            } else if (!borders.equals(other.borders))
                return false;
            return true;
        }
    }

    private static class Position {
        private int i;
        private int j;

        private Position(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "Position [i=" + i + ", j=" + j + "]";
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
    }

    private static class Transform {
        private final List<Position> border;
        private final Direction direction;

        private Transform(List<Position> border, Direction direction) {
            this.border = border;
            this.direction = direction;
        }
    }

    private static class Player {
        private final Position pos;
        private Direction dir;

        private Player(Position pos, Direction dir) {
            this.pos = pos;
            this.dir = dir;
        }

        @Override
        public String toString() {
            return "Player [pos=" + pos + ", dir=" + dir + "]";
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

    private static List<List<Position>> borders(Position[][] posMap) {
        final List<Position> north = new ArrayList<>();
        final List<Position> east = new ArrayList<>();
        final List<Position> south = new ArrayList<>();
        final List<Position> west = new ArrayList<>();

        for (int i = 0; i < posMap.length; i++) {
            for (int j = 0; j < posMap[0].length; j++) {
                if (i == 0) {
                    north.add(posMap[i][j]);
                }

                if (i == posMap.length - 1) {
                    south.add(posMap[i][j]);
                }

                if (j == 0) {
                    west.add(posMap[i][j]);
                }

                if (j == posMap[0].length - 1) {
                    east.add(posMap[i][j]);
                }
            }
        }

        return List.of(east, south, west, north);
    }

    private static List<Position> reverse(List<Position> border) {
        final List<Position> reversed = new ArrayList<>();
        for (int i = border.size() - 1; i >= 0; i--) {
            reversed.add(new Position(border.get(i).i, border.get(i).j));
        }
        return reversed;
    }

    private static CubeFace findFace(Map<CubeFace, List<Transform>> cubeMap, Position pos) {
        return cubeMap.keySet().stream().filter((c) -> c.points.contains(pos)).findFirst().get();
    }

    private static int findIndexOfPosition(CubeFace cf, Position pos) {
        for (final List<Position> bPos : cf.borders) {
            for (int i = 0; i < bPos.size(); i++) {
                if (bPos.get(i).equals(pos)) {
                    return i;
                }
            }
        }

        throw new RuntimeException("Index of position not found");
    }

    private static CubeFace buildCubeFace(int id, int depthMin, int depthMax, int lengthMin, int lengthMax) {
        int row = 0;
        int col = 0;

        final Set<Position> points = new HashSet<>();
        final Position[][] posMap = new Position[4][4];
        for (int i = depthMin; i <= depthMax; i++) {
            for (int j = lengthMin; j <= lengthMax; j++) {
                points.add(new Position(i, j));
                posMap[row][col++] = new Position(i, j);
            }
            ++row;
            col = 0;
        }

        return new CubeFace(id, points, borders(posMap));
    }

    private static Map<CubeFace, List<Transform>> buildCubeMap(char[][] map) {
        final Map<CubeFace, List<Transform>> cubeMap = new HashMap<>();
        int id = 0;

        final CubeFace c1 = buildCubeFace(++id, 0, 3, 8, 11);
        final CubeFace c2 = buildCubeFace(++id, 4, 7, 0, 3);
        final CubeFace c3 = buildCubeFace(++id, 4, 7, 4, 7);
        final CubeFace c4 = buildCubeFace(++id, 4, 7, 8, 11);
        final CubeFace c5 = buildCubeFace(++id, 8, 11, 8, 11);
        final CubeFace c6 = buildCubeFace(++id, 8, 11, 12, 15);

        //EAST
        //SOUTH
        //WEST
        //NORTH

        cubeMap.put(c1, List.of(
                            new Transform(reverse(c6.borders.get(Direction.EAST.value)), Direction.WEST),
                            new Transform(c4.borders.get(Direction.NORTH.value), Direction.SOUTH),
                            new Transform(c3.borders.get(Direction.NORTH.value), Direction.SOUTH),
                            new Transform(reverse(c2.borders.get(Direction.NORTH.value)), Direction.SOUTH)
        ));

        cubeMap.put(c2, List.of(
                            new Transform(c3.borders.get(Direction.WEST.value), Direction.EAST),
                            new Transform(reverse(c5.borders.get(Direction.SOUTH.value)), Direction.NORTH),
                            new Transform(reverse(c6.borders.get(Direction.SOUTH.value)), Direction.NORTH),
                            new Transform(reverse(c1.borders.get(Direction.NORTH.value)), Direction.SOUTH)
        ));

        cubeMap.put(c3, List.of(
                            new Transform(c4.borders.get(Direction.WEST.value), Direction.EAST),
                            new Transform(reverse(c5.borders.get(Direction.WEST.value)), Direction.EAST),
                            new Transform(c2.borders.get(Direction.EAST.value), Direction.WEST),
                            new Transform(c1.borders.get(Direction.WEST.value), Direction.EAST)
        ));

        cubeMap.put(c4, List.of(
                            new Transform(reverse(c6.borders.get(Direction.NORTH.value)), Direction.SOUTH),
                            new Transform(c5.borders.get(Direction.NORTH.value), Direction.SOUTH),
                            new Transform(c3.borders.get(Direction.EAST.value), Direction.WEST),
                            new Transform(c1.borders.get(Direction.SOUTH.value), Direction.NORTH)
        ));

        cubeMap.put(c5, List.of(
                            new Transform(c6.borders.get(Direction.WEST.value), Direction.EAST),
                            new Transform(reverse(c2.borders.get(Direction.SOUTH.value)), Direction.NORTH),
                            new Transform(reverse(c3.borders.get(Direction.SOUTH.value)), Direction.NORTH),
                            new Transform(c4.borders.get(Direction.SOUTH.value), Direction.NORTH)
        ));

        cubeMap.put(c6, List.of(
                            new Transform(reverse(c1.borders.get(Direction.EAST.value)), Direction.WEST),
                            new Transform(reverse(c2.borders.get(Direction.WEST.value)), Direction.EAST),
                            new Transform(c5.borders.get(Direction.EAST.value), Direction.WEST),
                            new Transform(reverse(c4.borders.get(Direction.EAST.value)), Direction.WEST)
        ));

        return cubeMap;
    }

    public static int findPassword(List<String> data) {
        final char[][] map = buildMap(data);
        final InstructionSupplier supplier = new InstructionSupplier(data.get(data.size() - 1));
        final Player player = new Player(new Position(0, data.get(0).indexOf('.')), Direction.EAST);

        final Map<Integer, int[]> lengthBounds = getMapLengthBounds(map);
        final Map<Integer, int[]> depthBounds = getMapDepthBounds(map);

        while (supplier.hasNext()) {
            final String instruction = supplier.next();
            if (instruction.length() == 1 && Character.isLetter(instruction.charAt(0))) {
                player.dir = player.dir.turn(instruction.charAt(0));
                continue;
            }

            int steps = Integer.parseInt(instruction);
            final int minBound;
            final int maxBound;
            int ii = player.pos.i;
            int jj = player.pos.j;

            switch(player.dir) {
                case EAST:
                    minBound = lengthBounds.get(player.pos.i)[0];
                    maxBound = lengthBounds.get(player.pos.i)[1];
                    while(steps > 0) {
                        ++jj;
                        if (jj == maxBound + 1) {
                            jj = minBound;
                        }

                        if (map[player.pos.i][jj] != '#') {
                            player.pos.j = jj;
                            --steps;
                            continue;
                        }
                        break;
                    }
                    break;
                case NORTH:
                    minBound = depthBounds.get(player.pos.j)[0];
                    maxBound = depthBounds.get(player.pos.j)[1];
                    while(steps > 0) {
                        --ii;
                        if (ii == minBound - 1) {
                            ii = maxBound;
                        }

                        if (map[ii][player.pos.j] != '#') {
                            player.pos.i = ii;
                            --steps;
                            continue;
                        }
                        break;
                    }
                    break;
                case SOUTH:
                    minBound = depthBounds.get(player.pos.j)[0];
                    maxBound = depthBounds.get(player.pos.j)[1];
                    while(steps > 0) {
                        ++ii;
                        if (ii == maxBound + 1) {
                            ii = minBound;
                        }

                        if (map[ii][player.pos.j] != '#') {
                            player.pos.i = ii;
                            --steps;
                            continue;
                        }
                        break;
                    }
                    break;
                case WEST:
                    minBound = lengthBounds.get(player.pos.i)[0];
                    maxBound = lengthBounds.get(player.pos.i)[1];
                    while(steps > 0) {
                        --jj;
                        if (jj == minBound - 1) {
                            jj = maxBound;
                        }

                        if (map[ii][jj] != '#') {
                            player.pos.j = jj;
                            --steps;
                            continue;
                        }
                        break;
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown direction: " + player.dir);
            }
        }

        return (1000 * (player.pos.i + 1)) + (4 * (player.pos.j + 1)) + player.dir.value;
    }

    private static Player transformPlayer(Map<CubeFace, List<Transform>> cubeMap, Player player) {
        final CubeFace cf = findFace(cubeMap, player.pos);
        final Transform transform = cubeMap.get(cf).get(player.dir.value);
        final int idx = findIndexOfPosition(cf, player.pos);
        final Position newPos = transform.border.get(idx);
        final Direction newDir = transform.direction;
        return new Player(newPos, newDir);
    }

    public static int findPasswordForAlteredMapTraversal(List<String> data) {
        final char[][] map = buildMap(data);
        final Map<CubeFace, List<Transform>> cubeMap = buildCubeMap(map);

        final InstructionSupplier supplier = new InstructionSupplier(data.get(data.size() - 1));
        final Player player = new Player(new Position(0, data.get(0).indexOf('.')), Direction.EAST);

        final Map<Integer, int[]> lengthBounds = getMapLengthBounds(map);
        final Map<Integer, int[]> depthBounds = getMapDepthBounds(map);

        while (supplier.hasNext()) {
            final String instruction = supplier.next();
            if (instruction.length() == 1 && Character.isLetter(instruction.charAt(0))) {
                player.dir = player.dir.turn(instruction.charAt(0));
                continue;
            }

            int steps = Integer.parseInt(instruction);

            while (steps != 0) {
                final int minBound;
                final int maxBound;
                int ii = player.pos.i;
                int jj = player.pos.j;
                Direction dir = player.dir;

                boolean terminate = false;

                switch(player.dir) {
                    case EAST:
                        maxBound = lengthBounds.get(player.pos.i)[1];
                        ++jj;

                        if (jj == maxBound + 1) {
                            final Player transformedPlayer = transformPlayer(cubeMap, player);
                            ii = transformedPlayer.pos.i;
                            jj = transformedPlayer.pos.j;
                            dir = transformedPlayer.dir;
                        }

                        if (map[ii][jj] != '#') {
                            player.pos.i = ii;
                            player.pos.j = jj;
                            player.dir = dir;
                            --steps;
                            continue;
                        }

                        terminate = true;
                        break;

                    case NORTH:
                        minBound = depthBounds.get(player.pos.j)[0];
                        --ii;

                        if (ii == minBound - 1) {
                            final Player transformedPlayer = transformPlayer(cubeMap, player);
                            ii = transformedPlayer.pos.i;
                            jj = transformedPlayer.pos.j;
                            dir = transformedPlayer.dir;
                        }

                        if (map[ii][jj] != '#') {
                            player.pos.i = ii;
                            player.pos.j = jj;
                            player.dir = dir;
                            --steps;
                            continue;
                        }

                        terminate = true;
                        break;

                    case SOUTH:
                        maxBound = depthBounds.get(player.pos.j)[1];
                        ++ii;

                        if (ii == maxBound + 1) {
                            final Player transformedPlayer = transformPlayer(cubeMap, player);
                            ii = transformedPlayer.pos.i;
                            jj = transformedPlayer.pos.j;
                            dir = transformedPlayer.dir;
                        }

                        if (map[ii][jj] != '#') {
                            player.pos.i = ii;
                            player.pos.j = jj;
                            player.dir = dir;
                            --steps;
                            continue;
                        }

                        terminate = true;
                        break;

                    case WEST:
                        minBound = lengthBounds.get(player.pos.i)[0];
                        --jj;

                        if (jj == minBound - 1) {
                            final Player transformedPlayer = transformPlayer(cubeMap, player);
                            ii = transformedPlayer.pos.i;
                            jj = transformedPlayer.pos.j;
                            dir = transformedPlayer.dir;
                        }

                        if (map[ii][jj] != '#') {
                            player.pos.i = ii;
                            player.pos.j = jj;
                            player.dir = dir;
                            --steps;
                            continue;
                        }

                        terminate = true;
                        break;

                    default:
                        throw new RuntimeException("Unknown direction: " + player.dir);
                }

                if (terminate) {
                    break;
                }
            }
        }

        return (1000 * (player.pos.i + 1)) + (4 * (player.pos.j + 1)) + player.dir.value;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day22.txt"));
        System.out.println("Part 1: " + findPassword(data));
        System.out.println("Part 2: " + findPasswordForAlteredMapTraversal(data));
    }
}
