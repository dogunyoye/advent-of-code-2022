package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Day05 {

    private interface CrateMover {
        void move(List<Cargo> cargos, List<Instruction> instructions);
    }

    static class CrateMover9000 implements CrateMover {
        @Override
        public void move(List<Cargo> cargos, List<Instruction> instructions) {
            for (final Instruction ins : instructions) {
                for (int i = 0; i < ins.numToMove; i++) {
                    final Cargo src = cargos.get(ins.srcCargo-1);
                    final Cargo dst = cargos.get(ins.dstCargo-1);
                    dst.add(src.remove());
                }
            }
        }
    }

    static class CrateMover9001 implements CrateMover {
        @Override
        public void move(List<Cargo> cargos, List<Instruction> instructions) {
            for (final Instruction ins : instructions) {
                final int numberToRemove = ins.numToMove;
                for (int i = 0; i < numberToRemove; i++) {
                    final Cargo src = cargos.get(ins.srcCargo-1);
                    final Cargo dst = cargos.get(ins.dstCargo-1);
    
                    final char[] crates = new char[numberToRemove];
                    for (int j = 0; j < numberToRemove; j++) {
                        crates[j] = src.remove();
                    }
                    for (int k = crates.length-1; k >= 0; k--) {
                        dst.add(crates[k]);
                    }
    
                    if (numberToRemove > 1) {
                        break;
                    }
                }
            }
        }
    }

    static record Cargo (Stack<Character> stack) {

        void add(char item) {
            this.stack.push(item);
        }

        char remove() {
            return this.stack.pop();
        }

        char inspect() {
            return this.stack.peek();
        }

        void reverse() {
            Collections.reverse(stack);
        }
    }

    static record Instruction (int numToMove, int srcCargo, int dstCargo) { }

    private static Map<Integer, Integer> indexToCargoId;

    static {
        indexToCargoId = new HashMap<>();
        indexToCargoId.put(1, 1);
        indexToCargoId.put(5, 2);
        indexToCargoId.put(9, 3);
        indexToCargoId.put(13, 4);
        indexToCargoId.put(17, 5);
        indexToCargoId.put(21, 6);
        indexToCargoId.put(25, 7);
        indexToCargoId.put(29, 8);
        indexToCargoId.put(33, 9);
    }

    static String findTopItems(CrateMover mover, List<Cargo> cargos, List<Instruction> instructions) {
        mover.move(cargos, instructions);
        String result = "";
        for (final Cargo c : cargos) {
            result += c.inspect();
        }
        return result;
    }

    static List[] parseCargoAndInstructions(List<String> cargoAndInstructions, int numberOfCargos) {
        final List[] l = new List[2];

        final List<Cargo> cargos = new ArrayList<>();
        final List<Instruction> ins = new ArrayList<>();

        int currIdx = 0;

        for (int i = 0; i < numberOfCargos; i++) {
            cargos.add(new Cargo(new Stack<Character>()));
        }

        for (final String line : cargoAndInstructions) {
            if (line.isEmpty()) {
                final List<String> instructions = cargoAndInstructions.subList(currIdx+1, cargoAndInstructions.size());
                for (final String instructionLine : instructions) {
                    final String[] s = instructionLine.split(" ");
                    ins.add(new Instruction(Integer.parseInt(s[1]), Integer.parseInt(s[3]), Integer.parseInt(s[5])));
                }

                for (Cargo c : cargos) {
                    c.reverse();
                }
                break;
            }

            for (int i = 0; i < line.length(); i++) {
                if (Character.isLetter(line.charAt(i))) {
                    final int crateId = indexToCargoId.get(i);
                    cargos.get(crateId-1).add(line.charAt(i));
                }
            }

            currIdx++;
        }

        l[0] = cargos;
        l[1] = ins;

        return l;
    }
    
    public static void main(String args[]) throws IOException {
        final List<String> cargoAndInstructions = Files.readAllLines(Path.of("src/main/resources/Day05.txt"));
        final List[] p1 = parseCargoAndInstructions(cargoAndInstructions, 9);
        final List[] p2 = parseCargoAndInstructions(cargoAndInstructions, 9);

        System.out.println("Part 1: " + findTopItems(new CrateMover9000(), p1[0], p1[1]));
        System.out.println("Part 2: " + findTopItems(new CrateMover9001(), p2[0], p1[1]));
    }
}
