package dev.jsinco.brewery.recipes;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                .brewDifficulty(recipe.getInt("brew-difficulty", 1))
                .recipeResult(recipeResultReader.readRecipeResult(recipe))
                .steps(parseSteps(recipe.getMapList("steps")))
                .build();
    }

    private @NotNull List<BrewingStep> parseSteps(List<Map<?, ?>> steps) {
        return steps.stream()
                .map(this::parseStep)
                .toList();
    }

    private BrewingStep parseStep(Map<?, ?> map) {
        BrewingStep.StepType type = BrewingStep.StepType.valueOf(String.valueOf(map.get("type")).toUpperCase(Locale.ROOT));
        return switch (type) {
            case COOK -> new BrewingStep.Cook(
                    new PassedMoment(((Integer) map.get("cook-time")).longValue() * PassedMoment.MINUTE),
                    ingredientManager.getIngredientsWithAmount((List<String>) map.get("ingredients")),
                    Registry.CAULDRON_TYPE.get(BreweryKey.parse(map.get("cauldron-type").toString().toLowerCase(Locale.ROOT)))
            );
            case DISTILL -> new BrewingStep.Distill(
                    (int) map.get("runs")
            );
            case AGE -> new BrewingStep.Age(
                    new PassedMoment(((Integer) map.get("age-years")).longValue() * Moment.AGING_YEAR),
                    Registry.BARREL_TYPE.get(BreweryKey.parse(map.get("barrel-type").toString().toLowerCase(Locale.ROOT)))
            );
            case MIX -> new BrewingStep.Mix(
                    new PassedMoment(((Integer) map.get("mix-time")).longValue() * Moment.MINUTE),
                    ingredientManager.getIngredientsWithAmount((List<String>) map.get("ingredients"))
            );
        };
    }

    public static int parseAlcoholString(String str) {
        return Integer.parseInt(str.replace("%", "").replace(" ", ""));
    }
}