package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

        private List<Recipe> getRecipes() {
            return List.of(oreRobotRecipe, clayRobotRecipe, obsidianRobotRecipe, geodeRobotRecipe);
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

        private List<Material> canBuild(Blueprint bp) {
            final List<Material> robots = new ArrayList<>();
            final List<Recipe> recipes = bp.getRecipes();

            recipes.forEach((r) -> {
                boolean canBuild = true;
                for (final Entry<Material, Integer> e : r.required.entrySet()) {
                    if (this.inventory.get(e.getKey()) < e.getValue()) {
                        canBuild = false;
                        break;
                    }
                }

                if (canBuild) {
                    robots.add(r.material);
                }
            });

            return robots;
        }

        private void build(Blueprint bp, Material robot) {
            final Recipe r = bp.getRecipes().stream().filter((recipe) -> recipe.material == robot).findFirst().get();
            for (final Entry<Material, Integer> e : r.required.entrySet()) {
                this.inventory.put(e.getKey(), this.inventory.get(e.getKey()) - e.getValue());
            }

            this.robots.add(robot);
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

            return new Factory(robotsCopy, inventoryCopy);
        }

        private void produce() {
            this.robots.forEach((m) -> {
                this.inventory.put(m, this.inventory.get(m) + 1);
            });
        }

        private long count(Material robot) {
            return this.robots.stream().filter((m) -> m == robot).count();
        }

        private State toState(int timeLeft) {
            final long oreRobots = this.robots.stream().filter((m) -> m == Material.ORE).count();
            final long clayRobots = this.robots.stream().filter((m) -> m == Material.CLAY).count();
            final long obsidianRobots = this.robots.stream().filter((m) -> m == Material.OBSIDIAN).count();
            final long geodeRobots = this.robots.stream().filter((m) -> m == Material.GEODE).count();

            return new State(inventory, oreRobots, clayRobots, obsidianRobots, geodeRobots, timeLeft);
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
        private final Map<Material, Integer> inventory;
        private final long oreRobots;
        private final long clayRobots;
        private final long obsidianRobots;
        private final long geodeRobots;
        private final int timeLeft;

        public State(Map<Material, Integer> inventory, long oreRobots, long clayRobots, long obsidianRobots,
                long geodeRobots, int timeLeft) {
            this.inventory = inventory;
            this.oreRobots = oreRobots;
            this.clayRobots = clayRobots;
            this.obsidianRobots = obsidianRobots;
            this.geodeRobots = geodeRobots;
            this.timeLeft = timeLeft;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
            result = prime * result + (int) (oreRobots ^ (oreRobots >>> 32));
            result = prime * result + (int) (clayRobots ^ (clayRobots >>> 32));
            result = prime * result + (int) (obsidianRobots ^ (obsidianRobots >>> 32));
            result = prime * result + (int) (geodeRobots ^ (geodeRobots >>> 32));
            result = prime * result + timeLeft;
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
            if (inventory == null) {
                if (other.inventory != null)
                    return false;
            } else if (!inventory.equals(other.inventory))
                return false;
            if (oreRobots != other.oreRobots)
                return false;
            if (clayRobots != other.clayRobots)
                return false;
            if (obsidianRobots != other.obsidianRobots)
                return false;
            if (geodeRobots != other.geodeRobots)
                return false;
            if (timeLeft != other.timeLeft)
                return false;
            return true;
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

        final long geodeRobots = factory.robots.stream().filter((r) -> r == Material.GEODE).count();
        final int currentGeodes = factory.inventory.get(Material.GEODE);
        final long maxPossibleGeodes = currentGeodes + (geodeRobots * timeLeft) + ((timeLeft * (timeLeft - 1))) / 2;

        if (maxPossibleGeodes <= maxGeodes) {
            return 0;
        }

        final State state = factory.toState(timeLeft);
        final Integer cached = memo.get(state);

        if (cached != null) {
            return cached;
        }

        int result = factory.inventory.get(Material.GEODE);
        final List<Material> canBuild = factory.canBuild(bp);

        for (final Material robot : canBuild) {
            if (robot == Material.GEODE || factory.count(robot) < bp.maxMaterials.get(robot)) {
                final Factory newFactory = factory.copy();
                newFactory.produce();
                newFactory.build(bp, robot);
                result = Math.max(result, findMaxGeodeProduction(newFactory, bp, timeLeft - 1, memo));
            }
        }

        factory.produce();
        result = Math.max(result, findMaxGeodeProduction(factory, bp, timeLeft - 1, memo));

        maxGeodes = Math.max(maxGeodes, result);

        memo.put(factory.toState(timeLeft), result);
        return result;
    }

    public static int calculateBlueprintQualityLevel(List<String> data) {
        final List<Blueprint> blueprints = createBlueprints(data);
        final Map<State, Integer> memo = new HashMap<>();
        int result = 0;

        for (final Blueprint bp : blueprints) {
            final Factory factory = new Factory();
            result += bp.id * findMaxGeodeProduction(factory, bp, 24, memo);
            memo.clear();
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
