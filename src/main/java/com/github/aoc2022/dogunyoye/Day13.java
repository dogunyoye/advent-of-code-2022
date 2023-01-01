package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;

public class Day13 {

    private static final JSONArray TWO = new JSONArray("[[2]]");
    private static final JSONArray SIX = new JSONArray("[[6]]");

    private enum Result {
        YES, NO, EQUAL
    }

    private static Result comparePackets(JSONArray left, JSONArray right) {
        final int max = Math.max(left.length(), right.length());

        for (int i = 0; i < max; i++) {

            if (i >= left.length()) {
                return Result.YES;
            }

            if (i >= right.length()) {
                return Result.NO;
            }

            final Object l = left.get(i);
            final Object r = right.get(i);

            Result result = null;

            if (l instanceof Integer && r instanceof Integer) {
                int lValue = (int) l;
                int rValue = (int) r;

                if (lValue < rValue) {
                    result = Result.YES;
                } else if (lValue > rValue) {
                    result = Result.NO;
                } else {
                    result = Result.EQUAL;
                }
            } else if (l instanceof JSONArray && r instanceof JSONArray) {
                result = comparePackets((JSONArray) l, (JSONArray) r);
            } else if (l instanceof JSONArray && r instanceof Integer) {
                result = comparePackets((JSONArray) l, new JSONArray("[" + r + "]"));
            } else if (l instanceof Integer && r instanceof JSONArray) {
                result = comparePackets(new JSONArray("[" + l + "]"), (JSONArray) r);
            }

            if (result == Result.YES || result == Result.NO) {
                return result;
            }
        }

        return Result.EQUAL;
    }

    static int parsePacketsAsJson(List<String> packetData, List<JSONArray> orderedPackets) {
        int result = 0;
        int pairNum = 0;

        for (int i = 0; i < packetData.size(); i += 3) {
            pairNum++;
            final JSONArray left = new JSONArray(packetData.get(i));
            final JSONArray right = new JSONArray(packetData.get(i+1));
            
            if (comparePackets(left, right) == Result.YES) {
                result += pairNum;
                orderedPackets.add(left);
                orderedPackets.add(right);
            } else {
                orderedPackets.add(right);
                orderedPackets.add(left);
            }
        }

        return result;
    }

    public static int calculateDecoderKey(List<JSONArray> packetData) {
        packetData.add(TWO);
        packetData.add(SIX);

        packetData.sort(new Comparator<JSONArray>() {
            @Override
            public int compare(JSONArray o1, JSONArray o2) {
                switch(comparePackets(o1, o2)) {
                    case EQUAL:
                        return 0;
                    case NO:
                        return 1;
                    case YES:
                        return -1;
                    default:
                        throw new RuntimeException("Unknown result");
                }
            }
        });

        final int twoIndex = packetData.indexOf(TWO) + 1;
        final int sixIndex = packetData.indexOf(SIX) + 1;

        return twoIndex * sixIndex;
    }

    
    public static void main(String[] args) throws IOException {
        final List<String> packetData = Files.readAllLines(Path.of("src/main/resources/Day13.txt"));
        final List<JSONArray> orderedPackets = new ArrayList<>();

        System.out.println("Part 1: " + parsePacketsAsJson(packetData, orderedPackets));
        System.out.println("Part 2: " + calculateDecoderKey(orderedPackets));
    }
}
