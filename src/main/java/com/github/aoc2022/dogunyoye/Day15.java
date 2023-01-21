package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day15 {

    private static record Bound (long min, long max) { }
    private static record Position (int x, int y) { }

    static class Sensor {
        private Position pos;
        private int manhattanDistance;

        Sensor(Position pos, Position beaconPos) {
            this.pos = pos;
            this.manhattanDistance =
                Math.abs(pos.x - beaconPos.x) + Math.abs(pos.y - beaconPos.y);
        }

        Bound getBound(int y) {

            // outside the sensor's range
            if (y > pos.y + manhattanDistance) {
                return null;
            }

            // outside the sensor's range
            if (y < pos.y - manhattanDistance) {
                return null;
            }

            int val = Math.abs(pos.y - y);
            final int min = pos.x - manhattanDistance + val;
            final int max = pos.x + manhattanDistance - val;
            return new Bound(min, max);
        }
    }

    static List<Sensor> parseSensors(List<String> data) {
        final List<Sensor> sensors = new ArrayList<>();
        for (final String line : data) {
            final String normalised = line.replaceAll(",", "").replaceAll(":", "");
            final String[] split = normalised.split(" ");
            final int sx = Integer.parseInt(split[2].substring(2, split[2].length()));
            final int sy = Integer.parseInt(split[3].substring(2, split[3].length()));
            final int bx = Integer.parseInt(split[8].substring(2, split[8].length()));
            final int by = Integer.parseInt(split[9].substring(2, split[9].length()));
            sensors.add(new Sensor(new Position(sx, sy), new Position(bx, by)));
        }

        return sensors;
    }

    static long getNumberOfPositionsWithNoBeacon(List<Sensor> sensors, int y) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (final Sensor s : sensors) {
            final Bound bound = s.getBound(y);
            if (bound != null) {
                min = Math.min(min, bound.min());
                max = Math.max(max, bound.max());
            }
        }

        return max - min;
    }

    private static boolean withinBounds(long value, List<Bound> bounds) {
        for (final Bound b : bounds) {
            if (b.min() <= value && value <= b.max()) {
                return true;
            }
        }

        return false;
    }

    static long calculateTuningFrequency(List<Sensor> sensors, int y) {

        for (int i = 0; i <= y; i++) {

            final List<Bound> bounds = new ArrayList<>();
            for (final Sensor s : sensors) {
                final Bound b = s.getBound(i);
                if (b != null) {
                    bounds.add(b);
                }
            }

            // sort bounds in ascending order,
            // in accordance to their minimum bound
            bounds.sort(new Comparator<Bound>() {
                @Override
                public int compare(Bound o1, Bound o2) {
                    return Long.compare(o1.min(), o2.min());
                } 
            });

            for (int j = 0; j < bounds.size() - 1; j++) {
                final long max = bounds.get(j).max();
                final long min = bounds.get(j+1).min();

                if (min > max && !withinBounds(min-1, bounds)) {
                    final long diff = min - max;
                    // diff of 2 => there is a point in between
                    // the max of one bound and the min of the other
                    if (diff == 2) {
                        // found the only point which is not detected by any sensors
                        // x = min - 1
                        // y = i
                        return ((min - 1) * 4000000) + i;
                    }
                }
            }
        }

        throw new RuntimeException("Value not found");
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day15.txt"));
        final List<Sensor> sensors = parseSensors(data);

        System.out.println("Part 1: " + getNumberOfPositionsWithNoBeacon(sensors, 2000000));
        System.out.println("Part 2: " + calculateTuningFrequency(sensors, 4000000));
    }
}
