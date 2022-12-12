package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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

    static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Integer.compare(o1.cost, o2.cost);
        }   
    }

    private static class Node {
        final int x;
        final int y;
        int cost;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.cost = 0;
        }

        public void setCost(int cost) {
            this.cost = cost;
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

    private static int djikstra(int[][] map, Node startNode, int length, int depth) {
        final PriorityQueue<Node> frontier = new PriorityQueue<>(length * depth, new NodeComparator());
        startNode.setCost(0);
        frontier.add(startNode);

        final Map<Node, Integer> costSoFar = new HashMap<>();
        final Map<Node, Node> prevMap = new HashMap<>();

        costSoFar.put(startNode, 0);

        while (!frontier.isEmpty()) {
            final Node current = frontier.remove();

            if (current.equals(end)) {
                break;
            }

            final int currentCost = current.cost;

            if (currentCost <= costSoFar.get(current)) {
                for (Node n : getNeighbours(map, current, length, depth)) {
    
                    final int w = map[n.x][n.y];
                    final int newCost = currentCost + w;

                    if (!costSoFar.containsKey(n) || newCost < costSoFar.get(n)) {
                        costSoFar.put(n, newCost);
    
                        final Node node = new Node(n.x, n.y);
                        prevMap.put(n, current);

                        node.setCost(newCost);
                        frontier.add(node);
                    }
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
        return djikstra(heightMap, start, length, depth);
    }
    
    public static int fewestStepsFromAtoBestSignalLocation(int[][] heightMap, int length, int depth) {
        final List<Integer> steps = new ArrayList<>();
        for (final Node n : aStartingPositions) {
            // 0 => no solution
            final int result = djikstra(heightMap, n, length, depth);
            if (result > 0) {
                steps.add(result);
            }
        }

        steps.sort(Integer::compare);
        return steps.get(0);
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
