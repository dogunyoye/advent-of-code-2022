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

        private Map<Material, Recipe> getRecipes() {
            return Map.of(
                Material.ORE, oreRobotRecipe,
                Material.CLAY, clayRobotRecipe,
                Material.OBSIDIAN, obsidianRobotRecipe,
                Material.GEODE, geodeRobotRecipe);
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
            final Map<Material, Recipe> recipes = bp.getRecipes();

            recipes.forEach((m, r) -> {
                boolean canBuild = true;
                for (final Entry<Material, Integer> e : r.required.entrySet()) {
                    if (this.inventory.get(e.getKey()) < e.getValue()) {
                        canBuild = false;
                        break;
                    }
                }

                if (canBuild) {
                    robots.add(m);
                }
            });

            return robots;
        }

        private void build(Blueprint bp, Material robot) {
            final Recipe r = bp.getRecipes().get(robot);
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

    private static int findMaxGeodeProduction(Factory factory, Blueprint bp, int timeLeft, Map<Integer, Integer> memo) {
        if (timeLeft == 0) {
            return factory.inventory.get(Material.GEODE);
        }

        factory.produce();

        if (memo.containsKey(timeLeft)) {
            final int maxGeodeAtTime = memo.get(timeLeft);
            if (factory.inventory.get(Material.GEODE) < maxGeodeAtTime) {
                return maxGeodeAtTime;
            }
        }

        int result = 0;
        final List<Material> canBuild = factory.canBuild(bp);

        for (final Material robot : canBuild) {
            if (robot == Material.GEODE || factory.count(robot) < bp.maxMaterials.get(robot)) {
                final Factory newFactory = factory.copy();
                newFactory.build(bp, robot);
                result = Math.max(result, findMaxGeodeProduction(newFactory, bp, timeLeft - 1, memo));
            }
        }

        result = Math.max(result, findMaxGeodeProduction(factory, bp, timeLeft - 1, memo));

        //result = Math.max(result, findMaxGeodeProduction(factory, bp, timeLeft - 1));
        memo.put(timeLeft, factory.inventory.get(Material.GEODE));
        return result;
    }

    public static int calculateBlueprintQualityLevel(List<String> data) {
        final List<Blueprint> blueprints = createBlueprints(data);
        final Map<Integer, Integer> memo = new HashMap<>();
        int result = 0;

        for (final Blueprint bp : blueprints) {
            System.out.println(findMaxGeodeProduction(new Factory(), bp, 24, memo));
            memo.clear();
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day19.txt"));
        System.out.println("Part 1: " + calculateBlueprintQualityLevel(data));
    }
}
