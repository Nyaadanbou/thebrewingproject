package dev.jsinco.brewery.recipes;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.Util;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeReader<R, I> {

    private final File folder;
    private final RecipeResultReader<R> recipeResultReader;
    private final IngredientManager<I> ingredientManager;

    public RecipeReader(File folder, RecipeResultReader<R> recipeResultReader, IngredientManager<I> ingredientManager) {
        this.folder = folder;
        this.recipeResultReader = recipeResultReader;
        this.ingredientManager = ingredientManager;
    }

    public Map<String, Recipe<R, I>> readRecipes() {
        Path mainDir = folder.toPath();
        YamlFile recipesFile = new YamlFile(mainDir.resolve("recipes.yml").toFile());

        try {
            recipesFile.createOrLoadWithComments();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ConfigurationSection recipesSection = recipesFile.getConfigurationSection("recipes");
        ImmutableMap.Builder<String, Recipe<R, I>> recipes = new ImmutableMap.Builder<>();
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
    private Recipe<R, I> getRecipe(ConfigurationSection recipe, String recipeName) {
        return new Recipe.Builder<R,I>(recipeName)
                .brewTime(recipe.getInt("brew-time", 0))
                .brewDifficulty(recipe.getInt("brew-difficulty", 1))
                .cauldronType(Registry.CAULDRON_TYPE.get(Registry.brewerySpacedKey(recipe.getString("cauldron-type", "water").toLowerCase(Locale.ROOT))))
                .ingredients(ingredientManager.getIngredientsWithAmount(recipe.getStringList("ingredients")))
                .distillRuns(recipe.getInt("distilling.runs", 0))
                .distillTime(recipe.getInt("distilling.time", 30))
                .barrelType(Registry.BARREL_TYPE.get(Registry.brewerySpacedKey(recipe.getString("barrel-type", "any").toLowerCase(Locale.ROOT))))
                .agingYears(recipe.getInt("aging.years", 0))
                .recipeResult(recipeResultReader.readRecipeResult(recipe))
                .build();
    }

    public static int parseAlcoholString(String str) {
        return Util.getIntDefaultZero(str.replace("%", "").replace(" ", ""));
    }

    // FIXME - I feel like there has to be a better way of doing this that doesn't rely on a map of enums?
    // This is ok from my point of view / thorinwasher
    public static Map<PotionQuality, String> getQualityFactoredString(String str) {
        if (!str.contains("/")) {
            return Map.of(PotionQuality.BAD, str, PotionQuality.GOOD, str, PotionQuality.EXCELLENT, str);
        }

        String[] list = str.split("/");
        if(list.length == 2) {
            throw new IllegalArgumentException("Expected a string with format <bad>/<good>/<excellent>");
        }
        Map<PotionQuality, String> map = new HashMap<>();
        for (int i = 0; i < Math.min(list.length, 3); i++) {
            map.put(PotionQuality.values()[i], list[i]);
        }
        return map;
    }

    public static Map<PotionQuality, List<String>> getQualityFactoredList(List<String> list) {
        Map<PotionQuality, List<String>> map = new HashMap<>();

        for (String string : list) {
            if (string.startsWith("+")) {
                map.put(PotionQuality.BAD, list);
            } else if (string.startsWith("++")) {
                map.put(PotionQuality.GOOD, list);
            } else if (string.startsWith("+++")) {
                map.put(PotionQuality.EXCELLENT, list);
            } else {
                for (PotionQuality quality : PotionQuality.values()) {
                    map.put(quality, list);
                }
            }
        }
        return map;
    }
}
