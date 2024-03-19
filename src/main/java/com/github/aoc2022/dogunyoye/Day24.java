package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day24 {

    private static class Position {
        private int i;
        private int j;

        private Position(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "Position [i=" + i + ", j=" + j + "]";
        }

        private List<Position> neighbours(MapData md) {

            final Position[] neighbours = new Position[]{
                new Position(i - 1, j),
                new Position(i, j + 1),
                new Position(i + 1, j),
                new Position(i, j - 1)
            };

            return Arrays.stream(neighbours)
                .filter((p) -> p.equals(md.start) || p.equals(md.end) || (p.i > 0 && p.i < md.maxDepth - 1 && p.j > 0 && p.j < md.maxLength - 1))
                .toList();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + i;
            result = prime * result + j;
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
            Position other = (Position) obj;
            if (i != other.i)
                return false;
            if (j != other.j)
                return false;
            return true;
        }
    }

    private static class Blizzard {
        private final char direction;
        private Position pos;

        private Blizzard(char direction, Position pos) {
            this.direction = direction;
            this.pos = pos;
        }

        private void move(int maxDepth, int maxLength) {
            switch (direction) {
                case '^':
                    --pos.i;
                    if (pos.i == 0) {
                        pos.i = maxDepth - 2;
                    }
                    break;
                case '>':
                    ++pos.j;
                    if (pos.j == maxLength - 1) {
                        pos.j = 1;
                    }
                    break;
                case 'v':
                    ++pos.i;
                    if (pos.i == maxDepth - 1) {
                        pos.i = 1;
                    }
                    break;
                case '<':
                    --pos.j;
                    if (pos.j == 0) {
                        pos.j = maxLength - 2;
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown blizzard direction: " + direction);
            }
        }

        @Override
        public String toString() {
            return "Blizzard [direction=" + direction + ", pos=" + pos + "]";
        }
    }

    private static class MapData {
        private final Position start;
        private final Position end;
        private final int maxDepth;
        private final int maxLength;

        private MapData(Position start, Position end, int maxDepth, int maxLength) {
            this.start = start;
            this.end = end;
            this.maxDepth = maxDepth;
            this.maxLength = maxLength;
        }
    }

    private static class State {
        private final int minutes;
        private final Position pos;

        private State(int minutes, Position pos) {
            this.minutes = minutes;
            this.pos = pos;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + minutes;
            result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
            if (minutes != other.minutes)
                return false;
            if (pos == null) {
                if (other.pos != null)
                    return false;
            } else if (!pos.equals(other.pos))
                return false;
            return true;
        }
    }

    private static boolean occupiedByBlizzard(Position pos, List<Blizzard> blizzards) {
        return blizzards.stream().anyMatch((b) -> b.pos.equals(pos));
    }

    private static List<Blizzard> buildBlizzards(List<String> data) {
        final int depth = data.size();
        final int length = data.get(0).length();
        final List<Blizzard> blizzards = new ArrayList<>();

        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < length; j++) {
                final char value = data.get(i).charAt(j);
                if (value == '^' || value == '>' || value == 'v' || value == '<') {
                    blizzards.add(new Blizzard(value, new Position(i, j)));
                }
            }
        }

        return blizzards;
    }

    private static List<Blizzard> copyBlizzards(List<Blizzard> blizzards) {
        final List<Blizzard> copy = new ArrayList<>();
        for (final Blizzard b : blizzards) {
            copy.add(new Blizzard(b.direction, new Position(b.pos.i, b.pos.j)));
        }
        return copy;
    }

    // TODO very slow, requires optimisation
    private static int traverseValley(List<String> data, MapData md, Queue<Position[]> destinations) {
        final int maxDepth = md.maxDepth;
        final int maxLength = md.maxLength;
        final Map<Integer, List<Blizzard>> blizzardMap = new HashMap<>();

        final Queue<State> queue = new ArrayDeque<>();
        int minutes = 0;

        blizzardMap.put(minutes, buildBlizzards(data));
        Position[] destination = destinations.poll();

        final State state = new State(minutes, destination[0]);
        queue.add(state);

        int result = 0;

        while (!queue.isEmpty()) {
            final State current = queue.poll();

            if (current.pos.equals(destination[1])) {
                result += current.minutes;
                if (destinations.isEmpty()) {
                    return result;
                }

                queue.clear();

                destination = destinations.poll();
                queue.add(new State(0, destination[0]));
                final List<Blizzard> blizzards = copyBlizzards(blizzardMap.get(current.minutes));
                blizzardMap.clear();
                minutes = 0;

                blizzardMap.put(minutes, blizzards);
                continue;
            }

            minutes = current.minutes + 1;
            final List<Blizzard> blizzards;

            if (!blizzardMap.containsKey(minutes)) {
                blizzards = copyBlizzards(blizzardMap.get(minutes - 1));
                for (final Blizzard b : blizzards) {
                    b.move(maxDepth, maxLength);
                }
                blizzardMap.put(minutes, blizzards);
            } else {
                blizzards = blizzardMap.get(minutes);
            }

            final List<Position> neighbours = current.pos.neighbours(md);

            List<Position> options = new ArrayList<>();
            options.addAll(neighbours);
            options.add(current.pos);

            options = options.stream().filter((pos) -> !occupiedByBlizzard(pos, blizzards)).toList();

            Set<State> queueSet;
            for (final Position p : options) {
                final State s = new State(minutes, p);
                queueSet = queue.stream().collect(Collectors.toSet());
                if (!queueSet.contains(s)) {
                    queue.add(s);
                }
            }
        }

        throw new RuntimeException("No solution!");
    }

    public static int findFewestMinutesToReachGoal(List<String> data) {
        final Position start = new Position(0, data.get(0).indexOf('.'));
        final Position end = new Position(data.size() - 1, data.get(data.size() - 1).indexOf('.'));
        final MapData md = new MapData(start, end, data.size(), data.get(0).length());

        final List<Position[]> destinations = new ArrayList<>();
        destinations.add(new Position[]{start, end});

        return traverseValley(data, md, new ArrayDeque<>(destinations));
    }

    public static int findFewestMinutesToMakeRoundtrip(List<String> data) {
        final Position start = new Position(0, data.get(0).indexOf('.'));
        final Position end = new Position(data.size() - 1, data.get(data.size() - 1).indexOf('.'));
        final MapData md = new MapData(start, end, data.size(), data.get(0).length());

        final List<Position[]> destinations = new ArrayList<>();
        destinations.add(new Position[]{start, end});
        destinations.add(new Position[]{end, start});
        destinations.add(new Position[]{start, end});

        return traverseValley(data, md, new ArrayDeque<>(destinations));
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day24.txt"));
        System.out.println("Part 1: " + findFewestMinutesToReachGoal(data));
        System.out.println("Part 2: " + findFewestMinutesToMakeRoundtrip(data));
    }
}
