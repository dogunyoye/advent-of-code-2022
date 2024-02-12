package com.github.aoc2022.dogunyoye;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

        private Blueprint(int id, Recipe oreRobotRecipe, Recipe clayRobotRecipe, Recipe obsidianRobotRecipe, Recipe geodeRobotRecipe) {
            this.id = id;
            this.oreRobotRecipe = oreRobotRecipe;
            this.clayRobotRecipe = clayRobotRecipe;
            this.obsidianRobotRecipe = obsidianRobotRecipe;
            this.geodeRobotRecipe = geodeRobotRecipe;
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

        private List<Material> canBuild(Blueprint bp) {
            final List<Material> robots = new ArrayList<>();
            final Map<Material, Recipe> recipes = bp.getRecipes();

            recipes.forEach((m0, r) -> {
                boolean canBuild = true;
                for (final Entry<Material, Integer> e : r.required.entrySet()) {
                    if (inventory.get(e.getKey()) < e.getValue()) {
                        canBuild = false;
                        break;
                    }
                }

                if (canBuild) {
                    robots.add(m0);
                }
            });

            return robots;
        }
    }

    private static List<Blueprint> createBlueprints(List<String> data) {
        final List<Blueprint> blueprints = new ArrayList<>();
        data.forEach((line) -> {
            final Matcher m = PATTERN.matcher(line);
            m.find();
            final int id = Integer.parseInt(m.group(1));
            final Recipe oreRobotRecipe = new Recipe(Material.ORE, Map.of(Material.ORE, Integer.parseInt(m.group(2))));
            final Recipe clayRobotRecipe = new Recipe(Material.CLAY, Map.of(Material.ORE, Integer.parseInt(m.group(3))));
            final Recipe obsidianRobotRecipe = new Recipe(Material.OBSIDIAN, Map.of(Material.ORE, Integer.parseInt(m.group(4)), Material.CLAY, Integer.parseInt(m.group(5))));
            final Recipe geodeRobotRecipe = new Recipe(Material.GEODE, Map.of(Material.ORE, Integer.parseInt(m.group(6)), Material.OBSIDIAN, Integer.parseInt(m.group(7))));
            blueprints.add(new Blueprint(id, oreRobotRecipe, clayRobotRecipe, obsidianRobotRecipe, geodeRobotRecipe));
        });

        return blueprints;
    }

    public static int calculateBlueprintQualityLevel(List<String> data) {
        final List<Blueprint> blueprints = createBlueprints(data);
        final Factory factory = new Factory();
        return 0;
    }

    public static void main(String[] args) throws IOException {
        final List<String> data = Files.readAllLines(Path.of("src/main/resources/Day19.txt"));
        System.out.println("Part 1: " + calculateBlueprintQualityLevel(data));
    }
}
