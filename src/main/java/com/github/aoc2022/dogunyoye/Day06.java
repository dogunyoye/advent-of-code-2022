package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Day06 {

    static class DataStreamParser {

        int findStartOfPacketMarker(String dataStream) {
            final Set<Character> uniqueChars = new HashSet<>();
            final char[] chars = dataStream.toCharArray();

            for (int i = 0; i < chars.length-3; i++) {
                uniqueChars.add(chars[i]);
                uniqueChars.add(chars[i+1]);
                uniqueChars.add(chars[i+2]);
                uniqueChars.add(chars[i+3]);

                if (uniqueChars.size() == 4) {
                    return i+4;
                }

                uniqueChars.clear();
            }

            throw new RuntimeException("Did not find start of packet marker");
        }

        int findStartOfMessageMarker(String dataStream) {
            final Set<Character> uniqueChars = new HashSet<>();
            final char[] chars = dataStream.toCharArray();

            for (int i = 0; i < chars.length-13; i++) {
                uniqueChars.add(chars[i]);

                for (int j = 1; j < 14; j++) {
                    if (!uniqueChars.add(chars[i+j])) {
                        break;
                    }
                }

                if (uniqueChars.size() == 14) {
                    return i+14;
                }

                uniqueChars.clear();
            }

            throw new RuntimeException("Did not find start of message marker");
        }
    }

    static int numberOfCharactersBeforeStartOfPacket(String data) {
        final DataStreamParser parser = new DataStreamParser();
        return parser.findStartOfPacketMarker(data);
    }

    static int numberOfCharactersBeforeStartOfMessage(String data) {
        final DataStreamParser parser = new DataStreamParser();
        return parser.findStartOfMessageMarker(data);
    }

    public static void main(String[] args) throws IOException {
        final String dataStream = Files.readAllLines(Path.of("src/main/resources/Day06.txt")).get(0);

        System.out.println("Part 1: " + numberOfCharactersBeforeStartOfPacket(dataStream));
        System.out.println("Part 2: " + numberOfCharactersBeforeStartOfMessage(dataStream));
    }
}
