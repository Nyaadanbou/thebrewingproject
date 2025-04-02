package dev.jsinco.brewery.recipes;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeReader<I, M> {

    private final File folder;
    private final RecipeResultReader<I, M> recipeResultReader;
    private final IngredientManager<I> ingredientManager;

    public RecipeReader(File folder, RecipeResultReader<I, M> recipeResultReader, IngredientManager<I> ingredientManager) {
        this.folder = folder;
        this.recipeResultReader = recipeResultReader;
        this.ingredientManager = ingredientManager;
    }

    public Map<String, Recipe<I, M>> readRecipes() {
        Path mainDir = folder.toPath();
        YamlFile recipesFile = new YamlFile(mainDir.resolve("recipes.yml").toFile());

        try {
            recipesFile.createOrLoadWithComments();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ConfigurationSection recipesSection = recipesFile.getConfigurationSection("recipes");
        ImmutableMap.Builder<String, Recipe<I, M>> recipes = new ImmutableMap.Builder<>();
        for (String recipeName : recipesSection.getKeys(false)) {
            recipes.put(recipeName, getRecipe(recipesSection.getConfigurationSection(recipeName), recipeName));
        }
        return recipes.build();
    }

    /**
     * Obtain a recipe from the recipes.yml file.
     *
     * @param recipeName The name/id of the recipe to obtain. Ex: 'example_recipe'
     * @return A Recipe object with all the attributes of the recipe.
     */
    private Recipe<I, M> getRecipe(ConfigurationSection recipe, String recipeName) {
        return new Recipe.Builder<I, M>(recipeName)
                .brewTime(recipe.getInt("brew-time", 0))
                .brewDifficulty(recipe.getInt("brew-difficulty", 1))
                .cauldronType(Registry.CAULDRON_TYPE.get(BreweryKey.parse(recipe.getString("cauldron-type", "water").toLowerCase(Locale.ROOT))))
                .ingredients(ingredientManager.getIngredientsWithAmount(recipe.getStringList("ingredients")))
                .distillRuns(recipe.getInt("distilling.runs", 0))
                .distillTime(recipe.getInt("distilling.time", 30))
                .barrelType(Registry.BARREL_TYPE.get(BreweryKey.parse(recipe.getString("aging.barrel-type", "any").toLowerCase(Locale.ROOT))))
                .agingYears(recipe.getInt("aging.years", 0))
                .recipeResult(recipeResultReader.readRecipeResult(recipe))
                .build();
    }

    public static int parseAlcoholString(String str) {
        return Integer.parseInt(str.replace("%", "").replace(" ", ""));
    }
}