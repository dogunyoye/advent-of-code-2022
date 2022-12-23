package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day17 {

    public static final long PART_TWO_ROCKS = 1000000000000L;

    private static class State {
        String shapeName;
        int windDirectionIdx;

        State(String shapeName, int windDirectionIdx) {
            this.shapeName = shapeName;
            this.windDirectionIdx = windDirectionIdx;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((shapeName == null) ? 0 : shapeName.hashCode());
            result = prime * result + windDirectionIdx;
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
            State other = (State) obj;
            if (shapeName == null) {
                if (other.shapeName != null)
                    return false;
            } else if (!shapeName.equals(other.shapeName))
                return false;
            if (windDirectionIdx != other.windDirectionIdx)
                return false;
            return true;
        }
    }

    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
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
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Point other = (Point) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            return true;
        }
    }

    private static class Cycle {
        long num;
        List<Long> values;
        long[] arr;

        Cycle(List<Long> diffs, int startIdx, int endIdx) {
            this.num = 0;
            this.values = new ArrayList<>();

            for (int i = startIdx; i < endIdx; i++) {
                this.values.add(diffs.get(i));
            }

            this.arr = values.stream().mapToLong(i -> i).toArray();
        }

        long select() {
            final long idx = num % values.size();
            final long value = arr[(int)idx];
            num++;

            return value;
        }
    }

    private static class RockSelector {
        static int num = 1;

        static void reset() {
            num = 1;
        }

        static Rock select() {
            final int toSelect = num % 5;
            ++num;

            switch(toSelect) {
                case 1:
                    return createHorizontalRock();
                case 2:
                    return createCrossRock();
                case 3:
                    return createLRock();
                case 4:
                    return createVerticalRock();
                case 0:
                    return createSquareRock();
                default:
                    throw new RuntimeException("Unknown rock");
            }
        }
    }

    private static class Rock {
        String name;
        List<Point> body;

        Rock(String name, List<Point> body) {
            this.name = name;
            this.body = body;
        }

        // level must less than, or equal to 0
        void moveUp(long level) {
            if (level > 0) {
                throw new RuntimeException("invalid level: " + level);
            }

            final List<Point> newPosition = new ArrayList<>();
            for (Point p : body) {
                newPosition.add(new Point(p.x - 3 + (int)level, p.y));
            }
            this.body = newPosition;
        }

        boolean moveLeft(List<Rock> settledRocks) {
            final List<Point> newPositions = new ArrayList<>();
            for (final Point p : body) {
                final int newY = p.y - 1;

                // blocked by left wall
                if (newY < 0) {
                    return false;
                }

                final Point newPoint = new Point(p.x, newY);
                if (rockClash(settledRocks, newPoint)) {
                    return false;
                }

                newPositions.add(newPoint);
            }

            this.body = newPositions;
            return true;
        }

        boolean moveRight(List<Rock> settledRocks) {
            final List<Point> newPositions = new ArrayList<>();
            for (final Point p : body) {
                final int newY = p.y + 1;

                // blocked by right wall
                if (newY > 6) {
                    return false;
                }

                final Point newPoint = new Point(p.x, newY);
                if (rockClash(settledRocks, newPoint)) {
                    return false;
                }

                newPositions.add(newPoint);
            }

            this.body = newPositions;
            return true;
        }

        boolean moveDown(List<Rock> settledRocks) {
            final List<Point> newPositions = new ArrayList<>();
            for (final Point p : body) {
                final int newX = p.x + 1;
                if (newX > 0) {
                    return false;
                }

                final Point newPoint = new Point(newX, p.y);
                if (rockClash(settledRocks, newPoint)) {
                    return false;
                }

                newPositions.add(newPoint);
            }

            this.body = newPositions;
            return true;
        }
    }

    /**
     * Return true if the rock currently being moved clashes
     * with a settled rock.
     * 
     * TODO: Optimise heavily.
     * Currently iterates over all settled rocks and compares
     * positions for equality.
     * Instead could store all occupied points in a hash set
     * and check if `mp` is stored within it.
     *
     * @param settled list of settled rocks
     * @param mp the new point on the moving rock
     * @return true if `mp` clashes with a settled rock.
     * false otherwise.
     */
    private static boolean rockClash(List<Rock> settled, Point mp) {
        for (final Rock settledRock : settled) {
            for (final Point p : settledRock.body) {
                if (p.equals(mp)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Rock createHorizontalRock() {
        final List<Point> body = new ArrayList<>();

        // ####

        for (int i = 0; i < 4; i++) {
            body.add(new Point(0, i + 2));
        }

        return new Rock("horizontal", body);
    }

    private static Rock createCrossRock() {
        final List<Point> body = new ArrayList<>();

        //  #
        // ###
        //  #
    
        body.add(new Point(-2, 3));
        
        for (int i = 0; i < 3; i++) {
            body.add(new Point(-1, i+2));
        }

        body.add(new Point(0, 3));

        return new Rock("cross", body);
    }

    private static Rock createLRock() {
        final List<Point> body = new ArrayList<>();

        //   #
        //   #
        // ###

        body.add(new Point(-2, 4));
        body.add(new Point(-1, 4));

        for (int i = 0; i < 3; i++) {
            body.add(new Point(0, i+2));
        }

        return new Rock("⅃", body);
    }

    private static Rock createVerticalRock() {
        final List<Point> body = new ArrayList<>();

        // #
        // #
        // #
        // #

        body.add(new Point(-3, 2));
        body.add(new Point(-2, 2));
        body.add(new Point(-1, 2));
        body.add(new Point(0, 2));

        return new Rock("vertical", body);
    }

    private static Rock createSquareRock() {
        final List<Point> body = new ArrayList<>();

        // ##
        // ##

        body.add(new Point(-1, 2));
        body.add(new Point(-1, 3));
        body.add(new Point(0, 2));
        body.add(new Point(0, 3));

        return new Rock("square", body);
    }

    private static int highestLevel(List<Rock> settledRocks) {
        int max = Integer.MAX_VALUE;
        for (final Rock r : settledRocks) {
            for (final Point p : r.body) {
                max = Math.min(max, p.x);
            }
        }

        return max - 1;
    }

    public static long findGreatestHeightFromSimulatingFallingRocks(String commands, long rocksToDrop) {

        final List<Rock> settledRocks = new ArrayList<>();
        List<Long> historyOfHeights = new ArrayList<>();

        long prev = 0;
        long dropped = 0;
        long highestRockLevel = 0;
        int idx = 0;

        final Map<State, List<Long>> cycle = new HashMap<>();

        final long initial = rocksToDrop;
        final int length = commands.length();

        RockSelector.reset();

    while (rocksToDrop != 0) {
            final Rock rock = RockSelector.select();
            rock.moveUp(highestRockLevel);

            final State state = new State(rock.name, (idx % length));
            final List<Long> value = new ArrayList<>(List.of(rocksToDrop, highestRockLevel));

            if (cycle.containsKey(state) && idx >= (3 * length)) {
                final List<Long> existing = cycle.get(state);

                final long interval = existing.get(0) - rocksToDrop;
                final long dx = Math.abs(highestRockLevel) - Math.abs(existing.get(1));

                long result = Math.abs(highestRockLevel);
                final long startIdx = initial - dropped;

                final Cycle c = new Cycle(historyOfHeights, (int)(initial - existing.get(0)), historyOfHeights.size());

                final long wholeCycles = startIdx / interval;
                final long remainder = (startIdx % interval);

                // how many whole cycles can I jump?
                for (long i = 0; i < wholeCycles; i++) {
                    result += dx;
                }

                // use the cycle selector to make up the
                // remaining steps.
                for (long i = 0; i < remainder; i++) {
                    result += c.select();
                }

                return result;
            } else {
                cycle.put(state, value);
            }

            while (true) {
                final int i = idx % length;
                switch(commands.charAt(i)) {
                    case '>':
                        rock.moveRight(settledRocks);
                        break;
                    case '<':
                        rock.moveLeft(settledRocks);
                        break;
                    default:
                        throw new RuntimeException("Unknown command: " + commands.charAt(i));
                }

                ++idx;

                if (!rock.moveDown(settledRocks)) {
                    settledRocks.add(rock);
                    break;
                }
            }

            prev = highestRockLevel;
            highestRockLevel = highestLevel(settledRocks);
            historyOfHeights.add(prev - highestRockLevel);

            --rocksToDrop;
            dropped++;
        }

        return Math.abs(highestRockLevel);
    }

    public static void main(String[] args) throws IOException {
        final String commands = Files.readAllLines(Path.of("src/main/resources/Day17.txt")).get(0);

        System.out.println("Part 1: " + findGreatestHeightFromSimulatingFallingRocks(commands, 2022));
        System.out.println("Part 2: " + findGreatestHeightFromSimulatingFallingRocks(commands, PART_TWO_ROCKS));
    }
}
