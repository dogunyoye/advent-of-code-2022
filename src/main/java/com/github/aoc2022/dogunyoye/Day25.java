package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day25 {

    private static long convertSNAFUToDecimal(String snafu) {
        long result = 0;
        for (int i = 0; i < snafu.length(); i++) {
            final char c = snafu.charAt(i);
            final int val;
            switch (c) {
                case '0':
                case '1':
                case '2':
                    val = Character.getNumericValue(c);
                    break;

                case '-':
                    val = -1;
                    break;
                
                case '=':
                    val = -2;
                    break;

                default:
                    throw new RuntimeException("Invalid character: " + c);
            }

            result += Math.pow(5, snafu.length() - 1 - i) * val;
        }

        return result;
    }

    private static String convertDecimalToSNAFU(long number) {
        long n = number;
        String s = "";

        while (n != 0) {
            final long rem = n % 5;
            n /= 5;

            switch ((int)rem) {
                case 0:
                case 1:
                case 2:
                    s = Long.toString(rem) + s;
                    break;
                
                case 3:
                    s = "=" + s;
                    ++n;
                    break;
                
                case 4:
                    s = "-" + s;
                    ++n;
                    break;
                
                default:
                    throw new RuntimeException("Invalid remainder: " + rem);
            }
        }
    
        return s;
    }

    public static String findSNAFUNumber(List<String> data) {
        return convertDecimalToSNAFU(data.stream().map(Day25::convertSNAFUToDecimal).mapToLong(i -> i).sum());
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day25.txt"));
        System.out.println("Part 1: " + findSNAFUNumber(data));
    }
}
