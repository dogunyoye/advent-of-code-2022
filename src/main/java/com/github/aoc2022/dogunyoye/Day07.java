package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Day07 {

    private static final Set<Directory> fileSystem = new HashSet<>();
    private static final int TOTAL_SPACE = 70000000;
    private static final int SPACE_NEEDED = 30000000;
    
    static class File {
        private String name;
        private int size;

        File(String name, int size) {
            this.name = name;
            this.size = size;
        }

        int getSize() {
            return this.size;
        }

        @Override
        public String toString() {
            return "File name: " + this.name + " Size: " + this.size;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
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

            File other = (File) obj;

            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }

            if (size != other.size) {
                return false;
            }

            return true;
        }
    }

    static class Directory {
        private String name;
        private Directory parent;
        private Set<Directory> directories;
        private Set<File> files;
        private int size;

        Directory(String name) {
            this.name = name;
            this.directories = new HashSet<>();
            this.files = new HashSet<>();
        }

        String getName() {
            return this.name;
        }

        Directory getParent() {
            return this.parent;
        }

        void addDirectory(Directory d) {
            this.directories.add(d);
        }

        Directory getDirectory(String name) {
            for (Directory d : directories) {
                if (d.name.equals(name)) {
                    return d;
                }
            }

            throw new RuntimeException("Directory of name: " + name + " does not exist");
        }

        void addFile(File f) {
            this.files.add(f);
        }

        void setParent(Directory parent) {
            this.parent = parent;
        }

        private int recurseDirectory(Directory directory) {
            int result = 0;

            for (File f : directory.files) {
                result += f.size;
            }

            for (Directory d : directory.directories) {
                result += recurseDirectory(d);
            }

            return result;
        }

        int getSize() {
            for (File f : this.files) {
                this.size += f.size;
            }

            for (Directory d : this.directories) {
                this.size += recurseDirectory(d);
            }

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

    static boolean isDirectory(String o) {
        return o.startsWith("dir");
    }

    static void parseCommandsAndOutput(Iterator<String> output, Directory currentDir) {

        while (output.hasNext()) {
            String[] parts = output.next().split(" ");
            final String command = parts[1];

            if (command.equals("ls")) {
                String next = output.next();

                while (next != null && !next.startsWith("$")) {
                    parts = next.split(" ");
                    final String name = parts[1];

                    if (isDirectory(next)) {
                        final Directory d = new Directory(name);
                        d.setParent(currentDir);

                        if (!fileSystem.contains(d)) {
                            fileSystem.add(d);
                            currentDir.addDirectory(d);
                        }
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
    

            } else if (command.equals("cd")) {
                final String dirName = parts[2];
                if (dirName.equals("..")) {
                    return;
                }

                parseCommandsAndOutput(output, currentDir.getDirectory(dirName));
      
            }
        }
    }

    static int findSumOfDirectoriesLessThan100000() {
        int sum = 0;
        for (Directory d : fileSystem) {
            int size = d.getSize();
            if (size <= 100000) {
                sum += size;
            }
        }

        return sum;
    }

    static int findSmallestDirectoryToDelete(Directory root) {

        final int remaining = TOTAL_SPACE - root.size;
        final int toClear = SPACE_NEEDED - remaining;

        int smallest = Integer.MAX_VALUE;
        for (Directory d : fileSystem) {
            if (d.size >= toClear) {
                smallest = Math.min(smallest, d.size);
            }
        }

        return smallest;
    }

    static void traverseFileSystem(List<String> output, Directory root) {
        fileSystem.add(root);
        final Iterator<String> iter = output.iterator();

        // skip first command
        iter.next();
    
        parseCommandsAndOutput(iter, root);
    }

    public static void main(String[] args) throws IOException {
        final List<String> output = Files.readAllLines(Path.of("src/main/resources/Day07.txt"));
        final Directory root = new Directory("/");
        traverseFileSystem(output, root);

        System.out.println("Part 1: " + findSumOfDirectoriesLessThan100000());
        System.out.println("Part 2: " + findSmallestDirectoryToDelete(root));
    }
}
