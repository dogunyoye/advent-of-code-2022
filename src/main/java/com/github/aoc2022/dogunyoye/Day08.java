package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day08 {

    static class Tree {
        private final int height;
        private final int x;
        private final int y;
        private int scenicScore;

        Tree(int x, int y, int height) {
            this.x = x;
            this.y = y;
            this.height = height;
        }

        int getHeight() {
            return this.height;
        }

        int getScenicScore() {
            return this.scenicScore;
        }

        void setScenicScore(int scenicScore) {
            this.scenicScore = scenicScore;
        }
    }
    
    static Tree[][] buildMap(List<String> grid, int length, int depth) {
        final Tree[][] map = new Tree[depth][length];
        for (int i = 0; i < depth; i++) {
            final String line = grid.get(i);
            for (int j = 0; j < length; j++) {
                final int height = Integer.parseInt(Character.toString(line.charAt(j)));
                map[i][j] = new Tree(i, j, height);
            }
        }

        return map;
    }

    private static List<List<Tree>> getSurroundingTrees(Tree[][] map, int depth, int length, Tree tree) {
        final List<List<Tree>> surroundingTrees = new ArrayList<>();

        final int x = tree.x;
        final int y = tree.y;

        final List<Tree> northernTrees = new ArrayList<>();
        for (int i = x-1; i >= 0; i--) {
            northernTrees.add(map[i][y]);
        }

        final List<Tree> westernTrees = new ArrayList<>();
        for (int i = y-1; i >= 0; i--) {
            westernTrees.add(map[x][i]);
        }

        final List<Tree> southernTrees = new ArrayList<>();
        for (int i = x+1; i < depth; i++) {
            southernTrees.add(map[i][y]);
        }

        final List<Tree> easternTrees = new ArrayList<>();
        for (int i = y+1; i < length; i++) {
            easternTrees.add(map[x][i]);
        }

        surroundingTrees.add(northernTrees);
        surroundingTrees.add(westernTrees);
        surroundingTrees.add(southernTrees);
        surroundingTrees.add(easternTrees);

        return surroundingTrees;
    }

    private static boolean checkTrees(List<Tree> trees, int height) {
        for (final Tree t : trees) {
            if (t.getHeight() >= height) {
                return false;
            }
        }

        return true;
    }

    private static boolean isTreeVisible(Tree[][] map, int depth, int length, Tree tree) {
        final int height = tree.getHeight();
        final List<List<Tree>> surroundingTrees = getSurroundingTrees(map, depth, length, tree);

        return (checkTrees(surroundingTrees.get(0), height) || checkTrees(surroundingTrees.get(1), height) || 
                checkTrees(surroundingTrees.get(2), height) || checkTrees(surroundingTrees.get(3), height));
    }

    private static int calculateScenicScore(List<Tree> trees, int height) {
        int score = 0;
        for (final Tree t : trees) {
            score++;
            if (t.getHeight() >= height) {
                return score;
            }
        }

        return score;
    }

    private static void calculateScenicScore(Tree[][] map, int depth, int length, Tree tree) {
        final int height = tree.getHeight();
        final List<List<Tree>> surroundingTrees = getSurroundingTrees(map, depth, length, tree);

        final int score = calculateScenicScore(surroundingTrees.get(0), height) * calculateScenicScore(surroundingTrees.get(1), height) *
                          calculateScenicScore(surroundingTrees.get(2), height) * calculateScenicScore(surroundingTrees.get(3), height);

        tree.setScenicScore(score);
    }

    static int findNumberOfVisibleTrees(Tree[][] map, int length, int depth) {
        int sum = 0;
        for (int i = 1; i <= depth-2; i++) {
            for (int j = 1; j <= length-2; j++) {
                if (isTreeVisible(map, depth, length, map[i][j])) {
                    sum++;
                }
            }
        }

        return sum + (length*2) + ((depth-2)*2);
    }

    static int findBestScenicScore(Tree[][] map, int length, int depth) {
        int bestScore = 0;
        for (int i = 1; i <= depth-2; i++) {
            for (int j = 1; j <= length-2; j++) {
                calculateScenicScore(map, depth, length, map[i][j]);
                bestScore = Math.max(bestScore, map[i][j].getScenicScore());
            }
        }

        return bestScore;
    }

    public static void main(String[] args) throws IOException {
        final List<String> grid = Files.readAllLines(Path.of("src/main/resources/Day08.txt"));
        final int length = grid.get(0).length();
        final int depth = grid.size();

        final Tree[][] map = buildMap(grid, length, depth);
        System.out.println("Part 1: " + findNumberOfVisibleTrees(map, length, depth));
        System.out.println("Part 2: " + findBestScenicScore(map, length, depth));
    }
}
