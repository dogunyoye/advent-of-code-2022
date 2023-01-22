package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day07 {

    private static final Set<Directory> fileSystem = new HashSet<>();
    private static final int TOTAL_SPACE = 70000000;
    private static final int SPACE_NEEDED = 30000000;
    private static final Directory ROOT_DIR = new Directory("/", null);

    private static record File (String name, int size) { }

    private static class Directory {
        private final String name;
        private Directory parent;
        private final Set<Directory> directories;
        private final Set<File> files;
        private int size;

        Directory(String name, Directory parent) {
            this.name = name;
            this.directories = new HashSet<>();
            this.files = new HashSet<>();
            this.parent = parent;
        }

        void addDirectory(Directory d) {
            this.directories.add(d);
        }

        Directory getDirectory(String name) {
            for (final Directory d : directories) {
                if (d.name.equals(name)) {
                    return d;
                }
            }

            throw new RuntimeException("Directory of name: '" + name + "' does not exist");
        }

        void addFile(File f) {
            this.files.add(f);
        }

        private int calculateSize(Directory directory) {
            int size = 0;

            for (File f : directory.files) {
                size += f.size();
            }

            for (Directory d : directory.directories) {
                size += calculateSize(d);
            }

            return size;
        }

        int calculateSize() {
            this.size = calculateSize(this);
            return this.size;
        }

        int size() {
            return this.size;
        }

        @Override
        public String toString() {
            return "Directory name: " + this.name + " Files: " + files.toString() + " Directories: " + directories.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((files == null) ? 0 : files.hashCode());
            result = prime * result + size;
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

            Directory other = (Directory) obj;

            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }

            if (parent == null) {
                if (other.parent != null) {
                    return false;
                }
            } else if (!parent.equals(other.parent)) {
                return false;
            }

            if (directories == null) {
                if (other.directories != null) {
                    return false;
                }
            } else if (!directories.equals(other.directories)) {
                return false;
            }

            if (files == null) {
                if (other.files != null) {
                    return false;
                }
            } else if (!files.equals(other.files)) {
                return false;
            }

            return true;
        }
    }

    private static boolean isDirectory(String o) {
        return o.startsWith("dir");
    }

    private static void parseCommandsAndOutput(Iterator<String> output, Directory currentDir) {

        while (output.hasNext()) {
            String[] parts = output.next().split(" ");
            final String command = parts[1];

            switch(command) {
                case "ls":
                    String next = output.next();
                    while (next != null && !next.startsWith("$")) {
                        parts = next.split(" ");
                        final String name = parts[1];

                        if (isDirectory(next)) {
                            final Directory d = new Directory(name, currentDir);
                            fileSystem.add(d);
                            currentDir.addDirectory(d);
                        } else {
                            final File f = new File(name, Integer.parseInt(parts[0]));
                            currentDir.addFile(f);
                        }

                        next = output.hasNext() ? output.next() : null;
                        if (next == null) {
                            // end of output
                            return;
                        }
                    }

                    final String dirName = next.split(" ")[2];
                    if (dirName.equals("..")) {
                        return;
                    }
                    
                    parseCommandsAndOutput(output, currentDir.getDirectory(dirName));
                    break;
                
                case "cd":
                    final String dName = parts[2];
                    if (dName.equals("..")) {
                        return;
                    }

                    parseCommandsAndOutput(output, currentDir.getDirectory(dName));
                    break;

                default:
                    throw new RuntimeException("Unknown command: " + command);
            }
        }
    }

    public static int findSumOfDirectoriesLessThan100000() {
        return fileSystem.stream().filter(d -> d.calculateSize() <= 100000)
            .collect(Collectors.summingInt(Directory::size));
    }

    public static int findSmallestDirectoryToDelete() {
        final int remaining = TOTAL_SPACE - ROOT_DIR.size;
        final int toClear = SPACE_NEEDED - remaining;

        return fileSystem.stream().filter(d -> d.calculateSize() >= toClear)
            .min((d1, d2) -> Integer.compare(d1.size(), d2.size())).get().size();
    }

    static void traverseFileSystem(List<String> output) {
        fileSystem.add(ROOT_DIR);
        final Iterator<String> iter = output.iterator();
        // skip first command
        iter.next();
        parseCommandsAndOutput(iter, ROOT_DIR);
    }

    public static void main(String[] args) throws IOException {
        final List<String> output = Files.readAllLines(Path.of("src/main/resources/Day07.txt"));
        traverseFileSystem(output);

        System.out.println("Part 1: " + findSumOfDirectoriesLessThan100000());
        System.out.println("Part 2: " + findSmallestDirectoryToDelete());
    }
}
