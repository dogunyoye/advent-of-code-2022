package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Day10 {

    static class CPU {
        private int X;
        private int cycles;
        private final List<Integer> signalStrengths;

        private String crtImage;
        private int currentPos = 0;
        private int spritePixelOnePos = 0;
        private int spritePixelTwoPos = 1;
        private int spritePixelThreePos = 2;

        CPU() {
            this.X = 1;
            this.cycles = 0;
            this.signalStrengths = new ArrayList<>();
            this.crtImage = "";
        }

        void noop() {
            incrementCycleAndCheck();
        }

        void addx(int value) {
            incrementCycleAndCheck();
            incrementCycleAndCheck();
            X += value;
        }

        List<Integer> getSignalStrengths() {
            return this.signalStrengths;
        }

        private void reset() {
            this.currentPos = 0;
            this.spritePixelOnePos = 0;
            this.spritePixelTwoPos = 1;
            this.spritePixelThreePos = 2;
        }

        private void incrementCycleAndCheck() {
            drawPixel();

            this.cycles++;
            switch(this.cycles) {
                case 20:
                case 60:
                case 100:
                case 140:
                case 180:
                case 220:
                    signalStrengths.add(this.X * this.cycles);
                    break;
                default:
                    break;
            }

            if (this.cycles%40 == 0) {
                reset();
            }
        }

        private void drawPixel() {

            if (this.currentPos == spritePixelOnePos ||
                this.currentPos == spritePixelTwoPos ||
                this.currentPos == spritePixelThreePos) {
                this.crtImage += '@';
            } else {
                this.crtImage += '.';
            }

            this.currentPos++;
        }

        void runProgram(Iterator<String> program) {
            while (program.hasNext()) {

                final String[] parts = program.next().split(" ");
                final String instruction = parts[0];

                switch(instruction) {
                    case "noop":
                        noop();
                        break;
                    case "addx":
                        addx(Integer.parseInt(parts[1]));
                        spritePixelOnePos = this.X - 1;
                        spritePixelTwoPos = this.X;
                        spritePixelThreePos = this.X + 1;
                        break;
                    default:
                        throw new RuntimeException("Unknown instruction: " + instruction);
                }
            }
        }
    }

    static int findSumOfSixSignalStrengths(CPU cpu) {
        int sum = 0;
        for (int signalStrength : cpu.getSignalStrengths()) {
            sum += signalStrength;
        }
        return sum;
    }

    static void printCRTImage(CPU cpu) {
        String line = "";
        for (int i = 0; i < cpu.crtImage.length(); i++) {
            line += cpu.crtImage.charAt(i);
            if ((i+1) % 40 == 0) {
                System.out.println(line);
                line = "";
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        final List<String> program = Files.readAllLines(Path.of("src/main/resources/Day10.txt"));
        final CPU cpu = new CPU();
        cpu.runProgram(program.iterator());

        System.out.println("Part 1: " + findSumOfSixSignalStrengths(cpu));
        System.out.println("Part 2: ");
        printCRTImage(cpu);
    }
}
