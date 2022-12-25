package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Day20 {

    static class Node {
        long value;
        int idx;

        Node(long value, int idx) {
            this.value = value;
            this.idx = idx;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (value ^ (value >>> 32));
            result = prime * result + idx;
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
            Node other = (Node) obj;
            if (value != other.value)
                return false;
            if (idx != other.idx)
                return false;
            return true;
        }
    }

    static List<Node> createNodes(List<String> data) {
        final List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            final int value = Integer.parseInt(data.get(i));
            nodes.add(new Node(value, i));
        }
        return nodes;
    }

    public static long findThreeNumberSum(List<Node> nodes, int mixes) {
        final LinkedList<Node> list = new LinkedList<Node>();
        for (final Node n : nodes) {
            list.add(n);
        }

        while (mixes != 0) {
            for (int i = 0; i < nodes.size(); i++) {
                final Node node = nodes.get(i);
                final long value = node.value;

                final int foundIdx = list.indexOf(node);
    
                if (foundIdx == -1) {
                    throw new RuntimeException("Did not find index of the node");
                }
    
                long insertionIdx = ((foundIdx + value) % (nodes.size() - 1));
                if (insertionIdx == foundIdx) {
                    continue;
                }

                if (insertionIdx < 0) {
                    insertionIdx = nodes.size() + insertionIdx - 1;
                }

                list.remove(foundIdx);
                list.add((int)insertionIdx, node);
            }

            mixes--;
        }

        int zeroIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).value == 0) {
                zeroIndex = i;
                break;
            }
        }

        if (zeroIndex == -1) {
            throw new RuntimeException("Invalid zero index");
        }

        final long a = list.get(((1000 + zeroIndex) % list.size())).value;
        final long b = list.get(((2000 + zeroIndex) % list.size())).value;
        final long c = list.get(((3000 + zeroIndex) % list.size())).value;

        return a + b + c;
    }

    public static long findThreeNumberSumWithDecryptionKeyApplied(List<Node> nodes, int mixes) {
        for (final Node n : nodes) {
            n.value *= 811589153;
        }

        return findThreeNumberSum(nodes, mixes);
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day20.txt"));

        System.out.println("Part 1: " + findThreeNumberSum(createNodes(data), 1));
        System.out.println("Part 2: " + findThreeNumberSumWithDecryptionKeyApplied(createNodes(data), 10));
    }
}
