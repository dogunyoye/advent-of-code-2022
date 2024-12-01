package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {

    private static int maxGeodes = 0;

    private static final Pattern PATTERN =
        Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

    private enum Material {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }

    private static class Recipe {
        private final Material material;
        private final Map<Material, Integer> required;

        private Recipe(Material material, Map<Material, Integer> required) {
            this.material = material;
            this.required = required;
        }

        @Override
        public String toString() {
            return "Recipe [material=" + material + ", required=" + required + "]";
        }
    }

    private static class Blueprint {
        private final int id;
        private final Recipe oreRobotRecipe;
        private final Recipe clayRobotRecipe;
        private final Recipe obsidianRobotRecipe;
        private final Recipe geodeRobotRecipe;
        private final Map<Material, Integer> maxMaterials;

        private Blueprint(int id,
            Recipe oreRobotRecipe,
            Recipe clayRobotRecipe,
            Recipe obsidianRobotRecipe,
            Recipe geodeRobotRecipe,
            Map<Material, Integer> maxMaterials) {
            this.id = id;
            this.oreRobotRecipe = oreRobotRecipe;
            this.clayRobotRecipe = clayRobotRecipe;
            this.obsidianRobotRecipe = obsidianRobotRecipe;
            this.geodeRobotRecipe = geodeRobotRecipe;
            this.maxMaterials = maxMaterials;
        }

        @Override
        public String toString() {
            return "Blueprint [id=" + id + ", oreRobotRecipe=" + oreRobotRecipe + ", clayRobotRecipe=" + clayRobotRecipe
                    + ", obsidianRobotRecipe=" + obsidianRobotRecipe + ", geodeRobotRecipe=" + geodeRobotRecipe + "]";
        }
    }

    private static class Factory {
        private final List<Material> robots;
        private final Map<Material, Integer> inventory;
        private int time = 0;

        private Factory() {
            this.robots = new ArrayList<>();
            this.robots.add(Material.ORE);

            this.inventory = new HashMap<>();
            this.inventory.put(Material.ORE, 0);
            this.inventory.put(Material.CLAY, 0);
            this.inventory.put(Material.OBSIDIAN, 0);
            this.inventory.put(Material.GEODE, 0);
        }

        private Factory(List<Material> robots, Map<Material, Integer> inventory) {
            this.robots = robots;
            this.inventory = inventory;
        }

        private Factory copy() {
            final Map<Material, Integer> inventoryCopy = new HashMap<>();
            final List<Material> robotsCopy = new ArrayList<>();

            this.inventory.forEach((m, c) -> {
                final int count = c;
                inventoryCopy.put(m, count);
            });

            this.robots.forEach((m) -> {
                robotsCopy.add(m);
            });

            final Factory newFactory = new Factory(robotsCopy, inventoryCopy);
            newFactory.time = this.time;

            return newFactory;
        }

        private int robots(Material robot) {
            return (int)this.robots.stream().filter((m) -> m == robot).count();
        }

        private State toState() {
            final long oreRobots = this.robots(Material.ORE);
            final long clayRobots = this.robots(Material.CLAY);
            final long obsidianRobots = this.robots(Material.OBSIDIAN);
            final long geodeRobots = this.robots(Material.GEODE);

            return new State(this.inventory.get(Material.ORE), this.inventory.get(Material.CLAY), this.inventory.get(Material.OBSIDIAN), this.inventory.get(Material.GEODE),
            oreRobots, clayRobots, obsidianRobots, geodeRobots, this.time);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((robots == null) ? 0 : robots.hashCode());
            result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
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
            Factory other = (Factory) obj;
            if (robots == null) {
                if (other.robots != null)
                    return false;
            } else if (!robots.equals(other.robots))
                return false;
            if (inventory == null) {
                if (other.inventory != null)
                    return false;
            } else if (!inventory.equals(other.inventory))
                return false;
            return true;
        }
    }

    private static class State {
        private final long ore;
        private final long clay;
        private final long obsidian;
        private final long geode;
        private final long oreRobots;
        private final long clayRobots;
        private final long obsidianRobots;
        private final long geodeRobots;
        private final int timeSpent;

        public State(long ore, long clay, long obsidian, long geode, long oreRobots, long clayRobots, long obsidianRobots,
                long geodeRobots, int timeSpent) {
            this.ore = ore;
            this.clay = clay;
            this.obsidian = obsidian;
            this.geode = geode;
            this.oreRobots = oreRobots;
            this.clayRobots = clayRobots;
            this.obsidianRobots = obsidianRobots;
            this.geodeRobots = geodeRobots;
            this.timeSpent = timeSpent;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (ore ^ (ore >>> 32));
            result = prime * result + (int) (clay ^ (clay >>> 32));
            result = prime * result + (int) (obsidian ^ (obsidian >>> 32));
            result = prime * result + (int) (geode ^ (geode >>> 32));
            result = prime * result + (int) (oreRobots ^ (oreRobots >>> 32));
            result = prime * result + (int) (clayRobots ^ (clayRobots >>> 32));
            result = prime * result + (int) (obsidianRobots ^ (obsidianRobots >>> 32));
            result = prime * result + (int) (geodeRobots ^ (geodeRobots >>> 32));
            result = prime * result + timeSpent;
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
            if (ore != other.ore)
                return false;
            if (clay != other.clay)
                return false;
            if (obsidian != other.obsidian)
                return false;
            if (geode != other.geode)
                return false;
            if (oreRobots != other.oreRobots)
                return false;
            if (clayRobots != other.clayRobots)
                return false;
            if (obsidianRobots != other.obsidianRobots)
                return false;
            if (geodeRobots != other.geodeRobots)
                return false;
            if (timeSpent != other.timeSpent)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "State [ore=" + ore + ", clay=" + clay + ", obsidian=" + obsidian + ", geode=" + geode
                    + ", oreRobots=" + oreRobots + ", clayRobots=" + clayRobots + ", obsidianRobots=" + obsidianRobots
                    + ", geodeRobots=" + geodeRobots + ", timeSpent=" + timeSpent + "]";
        }
    }

    private static List<Blueprint> createBlueprints(List<String> data) {
        final List<Blueprint> blueprints = new ArrayList<>();
        data.forEach((line) -> {
            final Matcher m = PATTERN.matcher(line);
            m.find();
            final int id = Integer.parseInt(m.group(1));
            final Map<Material, Integer> maxMaterials = new HashMap<>();
            final Recipe oreRobotRecipe = new Recipe(Material.ORE, Map.of(Material.ORE, Integer.parseInt(m.group(2))));
            final Recipe clayRobotRecipe = new Recipe(Material.CLAY, Map.of(Material.ORE, Integer.parseInt(m.group(3))));
            final Recipe obsidianRobotRecipe = new Recipe(Material.OBSIDIAN, Map.of(Material.ORE, Integer.parseInt(m.group(4)), Material.CLAY, Integer.parseInt(m.group(5))));
            final Recipe geodeRobotRecipe = new Recipe(Material.GEODE, Map.of(Material.ORE, Integer.parseInt(m.group(6)), Material.OBSIDIAN, Integer.parseInt(m.group(7))));

            maxMaterials.put(Material.ORE,
                Collections.max(List.of(oreRobotRecipe.required.get(Material.ORE), clayRobotRecipe.required.get(Material.ORE), obsidianRobotRecipe.required.get(Material.ORE), geodeRobotRecipe.required.get(Material.ORE))));

            maxMaterials.put(Material.CLAY, obsidianRobotRecipe.required.get(Material.CLAY));
            maxMaterials.put(Material.OBSIDIAN, geodeRobotRecipe.required.get(Material.OBSIDIAN));

            blueprints.add(new Blueprint(id, oreRobotRecipe, clayRobotRecipe, obsidianRobotRecipe, geodeRobotRecipe, maxMaterials));
        });

        return blueprints;
    }

    static int findMaxGeodeProduction(Factory factory, Blueprint bp, int timeLeft, Map<State, Integer> memo) {
        if (timeLeft == 0) {
            return factory.inventory.get(Material.GEODE);
        }

        final State state = factory.toState();
        final Integer cached = memo.get(state);

        if (cached != null) {
            return cached;
        }

        final int geodeRobots = factory.robots(Material.GEODE);
        final int currentGeodes = factory.inventory.get(Material.GEODE);
        final int maxPossibleGeodes = currentGeodes + (geodeRobots * timeLeft) + ((timeLeft * (timeLeft - 1))) / 2;

        if (maxPossibleGeodes <= maxGeodes) {
            return 0;
        }

        int result = factory.inventory.get(Material.GEODE);

        for (Material robot : Material.values()) {
            int oreNeeded = 0;
            int clayNeeded = 0;
            int obsidianNeeded = 0;

            switch(robot) {
                case CLAY:
                    oreNeeded = bp.clayRobotRecipe.required.get(Material.ORE);
                    break;
                case GEODE:
                    oreNeeded = bp.geodeRobotRecipe.required.get(Material.ORE);
                    obsidianNeeded = bp.geodeRobotRecipe.required.get(Material.OBSIDIAN);
                    break;
                case OBSIDIAN:
                    oreNeeded = bp.obsidianRobotRecipe.required.get(Material.ORE);
                    clayNeeded = bp.obsidianRobotRecipe.required.get(Material.CLAY);
                    break;
                case ORE:
                    oreNeeded = bp.oreRobotRecipe.required.get(Material.ORE);
                    break;
                default:
                    throw new RuntimeException("Unknown robot: " + robot);
            }

            if (robot != Material.GEODE && factory.robots(robot) >= bp.maxMaterials.get(robot)) {
                continue;
            }

            final int oreRobots = factory.robots(Material.ORE);
            final int clayRobots = factory.robots(Material.CLAY);
            final int obsidianRobots = factory.robots(Material.OBSIDIAN);

            // how much time do we need to gather enough materials to build the robot?

            if (robot == Material.OBSIDIAN && (clayNeeded > 0 && clayRobots == 0)) {
                continue;
            }

            if (robot == Material.GEODE && (obsidianNeeded > 0 && obsidianRobots == 0)) {
                continue;
            }

            int oreTimeNeeded = 0;
            int clayTimeNeeded = 0;
            int obsidianTimeNeeded = 0;

            final int oreCount = factory.inventory.get(Material.ORE);
            final int clayCount = factory.inventory.get(Material.CLAY);
            final int obsidianCount = factory.inventory.get(Material.OBSIDIAN);

            if (oreCount < oreNeeded) {
                oreTimeNeeded = (int) Math.ceil(((double)(oreNeeded - oreCount) / oreRobots));
            }

            if (clayCount < clayNeeded) {
                clayTimeNeeded = (int) Math.ceil(((double)(clayNeeded - clayCount) / clayRobots));
            }

            if (obsidianCount < obsidianNeeded) {
                obsidianTimeNeeded = (int) Math.ceil(((double)(obsidianNeeded - obsidianCount) / obsidianRobots));
            }

            final int timeNeeded = Collections.max(List.of(oreTimeNeeded, clayTimeNeeded, obsidianTimeNeeded)) + 1;
            final Factory newFactory = factory.copy();

            if (timeNeeded <= timeLeft) {
                newFactory.time += timeNeeded;
                newFactory.inventory.put(Material.ORE, (newFactory.inventory.get(Material.ORE) - oreNeeded) + (timeNeeded * newFactory.robots(Material.ORE)));
                newFactory.inventory.put(Material.CLAY, (newFactory.inventory.get(Material.CLAY) - clayNeeded) + (timeNeeded * newFactory.robots(Material.CLAY)));
                newFactory.inventory.put(Material.OBSIDIAN, (newFactory.inventory.get(Material.OBSIDIAN) - obsidianNeeded) + (timeNeeded * newFactory.robots(Material.OBSIDIAN)));
                newFactory.inventory.put(Material.GEODE, (newFactory.inventory.get(Material.GEODE) + (timeNeeded * newFactory.robots(Material.GEODE))));
                newFactory.robots.add(robot);
                result = Math.max(result, findMaxGeodeProduction(newFactory, bp, timeLeft - timeNeeded, memo));
            }

        }

        maxGeodes = Math.max(maxGeodes, result);
        memo.put(factory.toState(), result);
        return result;
    }

    public static int calculateBlueprintQualityLevel(List<String> data) {
        final List<Blueprint> blueprints = createBlueprints(data);
        int result = 0;

        for (final Blueprint bp : blueprints) {
            result += bp.id * findMaxGeodeProduction(new Factory(), bp, 24, new HashMap<State, Integer>());
            maxGeodes = 0;
        }

        return result;
    }

    public static long calculateProductOfTheLargestOpenGeodes(List<String> data) {
        final List<Blueprint> blueprints = createBlueprints(data);

        final int geodes0 = findMaxGeodeProduction(new Factory(), blueprints.get(0), 32, new HashMap<State, Integer>());
        maxGeodes = 0;

        final int geodes1 = findMaxGeodeProduction(new Factory(), blueprints.get(1), 32, new HashMap<State, Integer>());
        maxGeodes = 0;

        final int geodes2 = findMaxGeodeProduction(new Factory(), blueprints.get(2), 32, new HashMap<State, Integer>());
        maxGeodes = 0;

        return geodes0 * geodes1 * geodes2;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day19.txt"));
        System.out.println("Part 1: " + calculateBlueprintQualityLevel(data));
        System.out.println("Part 2: " + calculateProductOfTheLargestOpenGeodes(data));
    }
}
