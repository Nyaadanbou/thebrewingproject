package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.RecipeResult;
import dev.jsinco.brewery.recipes.ingredient.SimpleIngredient;
import dev.jsinco.brewery.util.moment.Interval;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BrewTest {
    RecipeRegistry registry;

    @BeforeEach
    void setUp() {
        MockBukkit.load(TheBrewingProject.class);
        registry = TheBrewingProject.getInstance().getRecipeRegistry();
    }

    @Test
    void closestRecipe_differingIngredients() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 11,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe2").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingBarrelType() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.BAMBOO);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe3").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingCauldronType() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.LAVA, BarrelType.ACACIA);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe4").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingDistillRuns() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 128, CauldronType.WATER, BarrelType.ACACIA);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe5").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingBrewTime() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 11), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe6").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingAgingTime() {
        setupRecipes();
        Brew brew = new Brew(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 10), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe closest = brew.closestRecipe().get();
        assertEquals(registry.getRecipe("recipe7").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(PotionQuality.EXCELLENT, brew.quality(closest).get());
    }

    void setupRecipes() {
        Recipe recipe1 = new Recipe.Builder("recipe1")
                .agingYears(13)
                .brewTime(10)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe2 = new Recipe.Builder("recipe2")
                .agingYears(13)
                .brewTime(10)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 11,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe3 = new Recipe.Builder("recipe3")
                .agingYears(13)
                .brewTime(10)
                .barrelType(BarrelType.BAMBOO)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe4 = new Recipe.Builder("recipe4")
                .agingYears(13)
                .brewTime(10)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.LAVA)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe5 = new Recipe.Builder("recipe5")
                .agingYears(13)
                .brewTime(10)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(128)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe6 = new Recipe.Builder("recipe6")
                .agingYears(13)
                .brewTime(11)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Recipe recipe7 = new Recipe.Builder("recipe7")
                .agingYears(10)
                .brewTime(10)
                .barrelType(BarrelType.ACACIA)
                .brewDifficulty(10)
                .cauldronType(CauldronType.WATER)
                .distillRuns(129)
                .ingredients(Map.of(
                        new SimpleIngredient(Material.WHEAT), 10,
                        new SimpleIngredient(Material.APPLE), 20
                ))
                .recipeResult(RecipeResult.GENERIC)
                .build();
        Map<String, Recipe> recipeMap = new HashMap<>();
        Stream.of(recipe1, recipe2, recipe3, recipe4, recipe5, recipe6, recipe7)
                .forEach(recipe -> recipeMap.put(recipe.getRecipeName(), recipe));
        registry.registerRecipes(recipeMap);
    }
}