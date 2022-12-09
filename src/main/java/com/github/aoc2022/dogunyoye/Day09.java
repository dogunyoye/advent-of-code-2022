package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Day09 {

    static class Position {
        private int x;
        private int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return this.x;
        }

        int getY() {
            return this.y;
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
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            Position other = (Position) obj;

            if (x != other.x) {
                return false;
            }

            if (y != other.y) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return "Position: [X: " + this.x + ", Y: " + this.y + "]";
        }
    }

    static class Knot {
        private Position pos;

        Knot(Position pos) {
            this.pos = pos;
        }

        Position getPosition() {
            return this.pos;
        }
    }

    private static boolean isHeadNeighbour(Knot head, Knot tail) {
        if (head.getPosition().equals(tail.getPosition())) {
            return true;
        }
    
        final int currentX = tail.getPosition().x;
        final int currentY = tail.getPosition().y;

        final Position north = new Position(currentX, currentY+1);
        final Position northEast = new Position(currentX+1, currentY+1);
        final Position east = new Position(currentX+1, currentY);
        final Position southEast = new Position(currentX+1, currentY-1);
        final Position south = new Position(currentX, currentY-1);
        final Position southWest = new Position(currentX-1, currentY-1);
        final Position west = new Position(currentX-1, currentY);
        final Position northWest = new Position(currentX-1, currentY+1);

        final Position[] neighbours =
            new Position[]{north, northEast, east, southEast, south, southWest, west, northWest};

        for (final Position n : neighbours) {
            if (n.equals(head.getPosition())) {
                return true;
            }
        }

        return false;
    }

    private static String whichDirection(Knot head, Knot tail) {

        final int headX = head.getPosition().x;
        final int headY = head.getPosition().y;

        final int tailX = tail.getPosition().x;
        final int tailY = tail.getPosition().y;

        final int dX = tailX - headX;
        final int dY = tailY - headY;

        if (dY == 0) {
            if (headX > tailX) {
                return "R"; 
            } else {
                return "L";
            }
        }

        if (dX == 0) {
            if (headY > tailY) {
                return "U";
            } else {
                return "D";
            }
        }

        if (dX < 0 && dY < 0) {
            return "RU";
        }

        if (dX < 0 && dY > 0) {
            return "DR";
        }

        if (dX > 0 && dY > 0) {
            return "LD";
        }

        return "LU";
    }

    private static void step(Knot k, String direction) {
        switch(direction) {
            // east
            case "R":
                k.getPosition().x++;
                break;
            // north
            case "U":
                k.getPosition().y++; 
                break;
            // north-east
            case "RU":
                k.getPosition().x++;
                k.getPosition().y++;
                break;
            // north-west
            case "LU":
                k.getPosition().x--;
                k.getPosition().y++;
                break;
            // west
            case "L":
                k.getPosition().x--;
                break;
            // south
            case "D":
                k.getPosition().y--;
                break;
            // south-west
            case "LD":
                k.getPosition().x--;
                k.getPosition().y--;
                break;
            // south-east
            case "DR":
                k.getPosition().x++;
                k.getPosition().y--;
                break;
            default:
                throw new RuntimeException("Unknown direction: " + direction);
        }
    }

    private static void move(Set<Position> path, Knot head, Knot tail, String direction, int steps) {
        while (steps != 0) {
            step(head, direction);
            while (!isHeadNeighbour(head, tail)) {
                final String tailDirection = whichDirection(head, tail);
                step(tail, tailDirection);
                path.add(new Position(tail.getPosition().x, tail.getPosition().y));
            }
            steps--;
        }
    }

    private static void moveKnots(Set<Position> path, Knot[] knots, String direction, int steps) {
        while (steps != 0) {
            step(knots[0], direction);
            for (int i = 1; i < knots.length; i++) {
                final Knot head = knots[i-1];
                final Knot tail = knots[i];
                while (!isHeadNeighbour(head, tail)) {
                    final String tailDirection = whichDirection(head, tail);
                    step(tail, tailDirection);

                    if (i == 9) {
                        path.add(new Position(tail.getPosition().x, tail.getPosition().y));
                    }
                }
            }
            steps--;
        }
    }

    static int numberOfStepsInTailPath(Iterator<String> instructions) {
        final Knot head = new Knot(new Position(0, 0));
        final Knot tail = new Knot(new Position(0, 0));

        final Set<Position> path = new HashSet<>();
        path.add(new Position(0, 0));

        while(instructions.hasNext()) {
            final String[] parts = instructions.next().split(" ");
            final String direction = parts[0];
            final int steps = Integer.parseInt(parts[1]);
            move(path, head, tail, direction, steps);
        }

        return path.size();
    }

    static int numberOfStepsInTailPathWith10Knots(Iterator<String> instructions) {
        final Knot[] knots = new Knot[10];
        for (int i = 0; i < 10; i++) {
            knots[i] = new Knot(new Position(0, 0));
        }

        final Set<Position> path = new HashSet<>();
        path.add(new Position(0, 0));

        while(instructions.hasNext()) {
            final String[] parts = instructions.next().split(" ");
            final String direction = parts[0];
            final int steps = Integer.parseInt(parts[1]);
            moveKnots(path, knots, direction, steps);
        }

        return path.size();
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> instructions = Files.readAllLines(Path.of("src/main/resources/Day09.txt"));

        System.out.println("Part 1: " + numberOfStepsInTailPath(instructions.iterator()));
        System.out.println("Part 2: " + numberOfStepsInTailPathWith10Knots(instructions.iterator()));
    }
}
