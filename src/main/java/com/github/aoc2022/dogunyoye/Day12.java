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

public class Day12 {

    private static Node start;
    private static Node end;

    private static List<Node> aStartingPositions = new ArrayList<>();

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    private static class Node {
        final int x;
        final int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof Node)) {
                return false;
            }

            return (x == ((Node) o).x && y == ((Node) o).y);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }

        @Override
        public String toString() {
            return String.format("[X:%d,Y:%d]", x , y);
        }
    }

    private static List<Node> getNeighbours(int[][]map, Node c, int length, int depth) {
        final List<Node> neighbours = new ArrayList<>();
        for (Direction d : Direction.values()) {
            int x = 0;
            int y = 0;

            switch(d) {
                case NORTH:
                    x = c.x - 1;
                    y = c.y;

                    if (x < 0) {
                        continue;
                    }
                    break;

                case EAST:
                    x = c.x;
                    y = c.y + 1;

                    if (y >= length) {
                        continue;
                    }
                    break;

                case SOUTH:
                    x = c.x + 1;
                    y = c.y;

                    if (x >= depth) {
                        continue;
                    }
                    break;

                case WEST:
                    x = c.x;
                    y = c.y - 1;

                    if (y < 0) {
                        continue;
                    }
                    break;
            }

            if (map[x][y] <= map[c.x][c.y] + 1) {
                neighbours.add(new Node(x, y));
            }
        }

        return neighbours;
    }

    private static int bfs(int[][] map, Node startNode, int length, int depth) {
        final Queue<Node> queue = new ArrayDeque<>();
        final Set<Node> explored = new HashSet<>();
        final Map<Node, Node> prevMap = new HashMap<>();

        explored.add(startNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            final Node v = queue.poll();
            if (v.equals(end)) {
                break;
            }

            final List<Node> neighbours = getNeighbours(map, v, length, depth);
            for (final Node n : neighbours) {
                if (!explored.contains(n)) {
                    explored.add(n);
                    prevMap.put(n, v);
                    queue.add(n);
                }
            }
        }

        return calculatePath(map, prevMap);
    }

    private static int calculatePath(int[][] map, Map<Node, Node> prevMap) {
        final List<Node> path = new ArrayList<>();
        Node prev = prevMap.get(end);

        while(prev != null) {
            path.add(prev);
            prev = prevMap.get(prev);
        }

        return path.size();
    }

    static int[][] buildHeightMap(List<String> grid, int length, int depth) {
        final int[][] heightMap = new int[depth][length];
        for (int i = 0; i < depth; i++) {
            final String row = grid.get(i);
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (c == 'S') {
                    start = new Node(i, j);
                    heightMap[i][j] = (int)'a';
                } else if (c == 'E') {
                    end = new Node(i, j);
                    heightMap[i][j] = (int)'z';
                } else {
                    heightMap[i][j] = (int)c;
                }

                if (c == 'a') {
                    aStartingPositions.add(new Node(i, j));
                }
            }
        }

        return heightMap;
    }

    public static int fewestStepsToBestSignalLocation(int[][] heightMap, int length, int depth) {
        return bfs(heightMap, start, length, depth);
    }
    
    public static int fewestStepsFromAtoBestSignalLocation(int[][] heightMap, int length, int depth) {
        int result = Integer.MAX_VALUE;
        for (final Node n : aStartingPositions) {
            // 0 => no solution
            final int steps = bfs(heightMap, n, length, depth);
            if (steps > 0) {
                result = Math.min(result, steps);
            }
        }

        return result;
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> grid = Files.readAllLines(Path.of("src/main/resources/Day12.txt"));
        final int length = grid.get(0).length();
        final int depth = grid.size();

        final int[][] heightMap = buildHeightMap(grid, length, depth);
        System.out.println("Part 1: " + fewestStepsToBestSignalLocation(heightMap, length, depth));
        System.out.println("Part 2: " + fewestStepsFromAtoBestSignalLocation(heightMap, length, depth));
    }
}
