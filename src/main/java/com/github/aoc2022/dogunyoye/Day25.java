package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day25 {

    private static int convertSNAFUToDecimal(String snafu) {
        int result = 0;
        for (int i = 0; i < snafu.length(); i++) {
            final char c = snafu.charAt(i);
            final int val;
            switch (c) {
                case '-':
                    val = -1;
                    break;
                
                case '=':
                    val = -2;
                    break;

                default:
                    val = Character.getNumericValue(c);
                    break;
            }

            result += Math.pow(5, snafu.length() - 1 - i) * val;
        }

        return result;
    }

    private static String convertDecimalToSnafu(int number) {
        return "";
    }

    public static int findSNAFUNumber(List<String> data) {
        final int sum = data.stream().map(Day25::convertSNAFUToDecimal).mapToInt(i -> i).sum();
        System.out.println(sum);

        return 0;
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day25.txt"));
        System.out.println("Part 1: " + findSNAFUNumber(data));
    }
}
