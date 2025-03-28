package dev.jsinco.brewery.bukkit.brews;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.bukkit.recipe.BukkitRecipeResult;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BrewTest {
    RecipeRegistry<ItemStack, PotionMeta> registry;

    @BeforeEach
    void setUp() {
        MockBukkit.load(TheBrewingProject.class);
        registry = TheBrewingProject.getInstance().getRecipeRegistry();
    }

    @Test
    void closestRecipe_differingIngredients() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 11,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe2").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingBarrelType() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.BAMBOO);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe3").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingCauldronType() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.LAVA, BarrelType.ACACIA);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe4").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingDistillRuns() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 128, CauldronType.WATER, BarrelType.ACACIA);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe5").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingBrewTime() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 11), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 13), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe6").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @Test
    void closestRecipe_differingAgingTime() {
        setupRecipes();
        Brew<ItemStack> brew = new Brew<>(new Interval(0, Interval.MINUTE * 10), Map.of(
                new SimpleIngredient(Material.WHEAT), 10,
                new SimpleIngredient(Material.APPLE), 20
        ), new Interval(0, Interval.AGING_YEAR * 10), 129, CauldronType.WATER, BarrelType.ACACIA);
        Recipe<ItemStack, PotionMeta> closest = brew.closestRecipe(registry).get();
        assertEquals(registry.getRecipe("recipe7").get(), closest);
        assertTrue(brew.hasCompletedRecipe(closest));
        assertEquals(BrewQuality.EXCELLENT, brew.quality(closest).get());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void quality_differingDifficulties(int difficulty) {
        Recipe<ItemStack, PotionMeta> recipe = new Recipe.Builder<ItemStack, PotionMeta>("test")
                .ingredients(Map.of(new SimpleIngredient(Material.WHEAT), 3))
                .brewTime(8)
                .brewDifficulty(difficulty)
                .agingYears(2)
                .barrelType(BarrelType.ANY)
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Brew<ItemStack> brew = new Brew<>(
                new PassedMoment(8 * Moment.MINUTE),
                Map.of(new SimpleIngredient(Material.WHEAT), 3),
                new PassedMoment(2 * Moment.AGING_YEAR),
                0,
                CauldronType.WATER,
                BarrelType.BAMBOO
        );
        assertEquals(BrewQuality.EXCELLENT, brew.quality(recipe).get());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void quality_increasingIngredient(int difficulty) {
        Recipe<ItemStack, PotionMeta> recipe = new Recipe.Builder<ItemStack, PotionMeta>("test")
                .ingredients(Map.of(new SimpleIngredient(Material.WHEAT), 3))
                .brewTime(8)
                .brewDifficulty(difficulty)
                .agingYears(2)
                .barrelType(BarrelType.ANY)
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        boolean hasHadNullQuality = false;
        for (int i = 3; i < 10; i++) {
            Brew<ItemStack> brew = new Brew<>(
                    new PassedMoment(8 * Moment.MINUTE),
                    Map.of(new SimpleIngredient(Material.WHEAT), i),
                    new PassedMoment(2 * Moment.AGING_YEAR),
                    0,
                    CauldronType.WATER,
                    BarrelType.BAMBOO
            );
            BrewQuality brewQuality = brew.quality(recipe).orElse(null);
            if (brewQuality == null) {
                hasHadNullQuality = true;
            }
        }
        assertTrue(hasHadNullQuality);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void quality_decreasingIngredient(int difficulty) {
        Recipe<ItemStack, PotionMeta> recipe = new Recipe.Builder<ItemStack, PotionMeta>("test")
                .ingredients(Map.of(new SimpleIngredient(Material.WHEAT), 3))
                .brewTime(8)
                .brewDifficulty(difficulty)
                .agingYears(2)
                .barrelType(BarrelType.ANY)
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        boolean hasHadNullQuality = false;
        for (int i = 3; i >= 0; i--) {
            Brew<ItemStack> brew = new Brew<>(
                    new PassedMoment(8 * Moment.MINUTE),
                    Map.of(new SimpleIngredient(Material.WHEAT), i),
                    new PassedMoment(2 * Moment.AGING_YEAR),
                    0,
                    CauldronType.WATER,
                    BarrelType.BAMBOO
            );
            BrewQuality brewQuality = brew.quality(recipe).orElse(null);
            if (brewQuality == null) {
                hasHadNullQuality = true;
            }
        }
        assertTrue(hasHadNullQuality);
    }

    void setupRecipes() {
        Recipe<ItemStack, PotionMeta> recipe1 = new Recipe.Builder<ItemStack, PotionMeta>("recipe1")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe2 = new Recipe.Builder<ItemStack, PotionMeta>("recipe2")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe3 = new Recipe.Builder<ItemStack, PotionMeta>("recipe3")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe4 = new Recipe.Builder<ItemStack, PotionMeta>("recipe4")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe5 = new Recipe.Builder<ItemStack, PotionMeta>("recipe5")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe6 = new Recipe.Builder<ItemStack, PotionMeta>("recipe6")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Recipe<ItemStack, PotionMeta> recipe7 = new Recipe.Builder<ItemStack, PotionMeta>("recipe7")
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
                .recipeResult(BukkitRecipeResult.GENERIC)
                .build();
        Map<String, Recipe<ItemStack, PotionMeta>> recipeMap = new HashMap<>();
        Stream.of(recipe1, recipe2, recipe3, recipe4, recipe5, recipe6, recipe7)
                .forEach(recipe -> recipeMap.put(recipe.getRecipeName(), recipe));
        registry.registerRecipes(recipeMap);
    }
}