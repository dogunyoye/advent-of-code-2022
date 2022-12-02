package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day02 {

    private enum Item {
        ROCK,
        PAPER,
        SCISSORS;

        private Item predator() {
            switch(this) {
                case ROCK:
                    return PAPER;
                case PAPER:
                    return SCISSORS;
                case SCISSORS:
                    return ROCK;
                default:
                    throw new RuntimeException("Invalid item");
            }
        }

        private Item prey() {
            switch(this) {
                case ROCK:
                    return SCISSORS;
                case PAPER:
                    return ROCK;
                case SCISSORS:
                    return PAPER;
                default:
                    throw new RuntimeException("Invalid item");
            }
        }
    }

    private enum Result {
        WIN,
        LOSE,
        DRAW
    }

    private static Map<String, Item> map;
    private static Map<Item, Integer> scoreMap;
    private static Map<String, Result> resultMap;

    static {
        map = new HashMap<>();
        scoreMap = new HashMap<>();
        resultMap = new HashMap<>();

        map.put("A", Item.ROCK);
        map.put("X", Item.ROCK);
        map.put("B", Item.PAPER);
        map.put("Y", Item.PAPER);
        map.put("C", Item.SCISSORS);
        map.put("Z", Item.SCISSORS);

        scoreMap.put(Item.ROCK, 1);
        scoreMap.put(Item.PAPER, 2);
        scoreMap.put(Item.SCISSORS, 3);

        resultMap.put("X", Result.LOSE);
        resultMap.put("Y", Result.DRAW);
        resultMap.put("Z", Result.WIN);
    }

    private static int playRockPaperScissors(Item opponentItem, Item myItem) {
        if (opponentItem == myItem) {
            return 3 + scoreMap.get(myItem);
        }

        if(myItem.prey() == opponentItem) {
            return 6 + scoreMap.get(myItem);
        }

        return scoreMap.get(myItem);
    }

    private static int playRockPaperScissorsStrategy(Item opponentItem, Result result) {
        Item myItem;
        switch(result) {
            case WIN:
                myItem = opponentItem.predator();
                return 6 + scoreMap.get(myItem);
            case LOSE:
                myItem = opponentItem.prey();
                return scoreMap.get(myItem);
            case DRAW:
                return 3 + scoreMap.get(opponentItem);
            default:
                throw new RuntimeException("Invalid result");
        }
    }

    public static int calculateScore(List<String> game) {
        int score = 0;
        for (String round : game) {
            final String[] items = round.split(" ");
            final Item oItem = map.get(items[0]);
            final Item mItem = map.get(items[1]);
            score += playRockPaperScissors(oItem, mItem);
        }

        return score;
    }

    public static int calculateNewStrategyScore(List<String> game) {
        int score = 0;
        for (String round : game) {
            final String[] items = round.split(" ");
            final Item oItem = map.get(items[0]);
            final Result result = resultMap.get(items[1]);
            score += playRockPaperScissorsStrategy(oItem, result);
        }

        return score;
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> game = Files.readAllLines(Path.of("src/main/resources/Day02.txt"));

        System.out.println("Part 1: " + calculateScore(game));
        System.out.println("Part 2: " + calculateNewStrategyScore(game));
    }
}
