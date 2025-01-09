package dev.jsinco.brewery.recipes;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Interval;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.Util;
import org.bukkit.potion.PotionEffectType;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.nio.file.Path;
import java.util.*;

public class RecipeFactory {

    private RecipeFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Recipe> readRecipes() {
        Path mainDir = TheBrewingProject.getInstance().getDataFolder().toPath();
        FileUtil.extractFile(TheBrewingProject.class, "recipes.yml", mainDir, false);
        YamlFile recipesFile = new YamlFile(mainDir.resolve("recipes.yml").toFile());

        try {
            recipesFile.createOrLoadWithComments();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ConfigurationSection recipesSection = recipesFile.getConfigurationSection("recipes");
        ImmutableMap.Builder<String, Recipe> recipes = new ImmutableMap.Builder<>();
        for (String recipeName : recipesSection.getKeys(false)) {
            recipes.put(recipeName, getRecipe(recipesSection.getConfigurationSection(recipeName), recipeName));
        }
        return recipes.build();
    }

    public static Map<String, DefaultRecipe> readDefaultRecipes() {
        Path mainDir = TheBrewingProject.getInstance().getDataFolder().toPath();
        FileUtil.extractFile(TheBrewingProject.class, "recipes.yml", mainDir, false);
        YamlFile recipesFile = new YamlFile(mainDir.resolve("recipes.yml").toFile());

        try {
            recipesFile.createOrLoadWithComments();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ConfigurationSection recipesSection = recipesFile.getConfigurationSection("recipes");
        ImmutableMap.Builder<String, DefaultRecipe> recipes = new ImmutableMap.Builder<>();
        for (String recipeName : recipesSection.getKeys(false)) {
            recipes.put(recipeName, getDefaultRecipe(recipesSection.getConfigurationSection(recipeName)));
        }
        return recipes.build();
    }

    public static DefaultRecipe getDefaultRecipe(ConfigurationSection defaultRecipe) {
        return new DefaultRecipe.Builder()
                .name(defaultRecipe.getString("name", "Cauldron Brew"))
                .lore(defaultRecipe.getStringList("lore"))
                .color(Util.parseColorString(defaultRecipe.getString("color", "BLUE")))
                .customModelData(defaultRecipe.getInt("custom-model-data", -1))
                .glint(defaultRecipe.getBoolean("glint", false))
                .build();
    }

    /**
     * Obtain a recipe from the recipes.yml file.
     *
     * @param recipeName The name/id of the recipe to obtain. Ex: 'example_recipe'
     * @return A Recipe object with all the attributes of the recipe.
     */
    private static Recipe getRecipe(ConfigurationSection recipe, String recipeName) {
        return new Recipe.Builder(recipeName)
                .brewTime(recipe.getInt("brew-time", 0))
                .brewDifficulty(recipe.getInt("brew-difficulty", 1))
                .cauldronType(Registry.CAULDRON_TYPE.get(Registry.brewerySpacedKey(recipe.getString("cauldron-type", "water").toLowerCase(Locale.ROOT))))
                .ingredients(IngredientManager.getIngredientsWithAmount(recipe.getStringList("ingredients")))
                .distillRuns(recipe.getInt("distilling.runs", 0))
                .distillTime(recipe.getInt("distilling.time", 30))
                .barrelType(Registry.BARREL_TYPE.get(Registry.brewerySpacedKey(recipe.getString("barrel-type", "any").toLowerCase(Locale.ROOT))))
                .agingYears(recipe.getInt("aging.years", 0))
                .recipeResult(getRecipeResult(recipe))
                .build();
    }

    private static RecipeResult getRecipeResult(ConfigurationSection configurationSection) {
        return new RecipeResult.Builder()
                .recipeEffects(getRecipeEffects(configurationSection))
                .customModelData(configurationSection.getInt("potion-attributes.custom-model-data", -1))
                .lore(getQualityFactoredList(configurationSection.getStringList("potion-attributes.lore")))
                .glint(configurationSection.getBoolean("potion-attributes.glint", false))
                .names(getQualityFactoredString(configurationSection.getString("potion-attributes.name")))
                .color(Util.parseColorString(configurationSection.getString("potion-attributes.color")))
                .build();
    }

    private static RecipeEffects getRecipeEffects(ConfigurationSection configurationSection) {
        return new RecipeEffects.Builder()
                .actionBar(configurationSection.getString("messages.action-bar", null))
                .title(configurationSection.getString("messages.title", null))
                .message(configurationSection.getString("messages.message", null))
                .commands(getQualityFactoredList(configurationSection.getStringList("commands")))
                .effects(getEffectsFromStringList(configurationSection.getStringList("effects")))
                .alcohol(parseAlcoholString(configurationSection.getString("alcohol", "0%")))
                .build();
    }


    // TODO: This should all be in a utility class

    private static int parseAlcoholString(String str) {
        return Util.getInt(str.replace("%", "").replace(" ", ""));
    }

    // FIXME - I feel like there has to be a better way of doing this that doesn't rely on a map of enums?
    private static Map<PotionQuality, String> getQualityFactoredString(String str) {
        if (!str.contains("/")) {
            return Map.of(PotionQuality.BAD, str, PotionQuality.GOOD, str, PotionQuality.EXCELLENT, str);
        }

        String[] list = str.split("/");
        Map<PotionQuality, String> map = new HashMap<>();

        for (int i = 0; i < Math.min(list.length, 3); i++) {
            map.put(PotionQuality.values()[i], list[i]);
        }
        // TODO: What if there's a missing potion quality
        return map;
    }

    private static Map<PotionQuality, List<String>> getQualityFactoredList(List<String> list) {
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

    private static Interval parseInterval(String string) {
        if (!string.contains("-")) {
            int i = Util.getInt(string);
            return new Interval(i, i);
        }
        String[] split = string.split("-");
        return new Interval(Util.getInt(split[0]), Util.getInt(split[1]));
    }


    private static List<RecipeEffect> getEffectsFromStringList(List<String> list) {
        List<RecipeEffect> effects = new ArrayList<>();
        for (String string : list) {
            effects.add(getEffect(string));
        }
        return effects;
    }


    private static RecipeEffect getEffect(String string) {
        if (!string.contains("/")) {
            return RecipeEffect.of(PotionEffectType.getByName(string), new Interval(1, 1), new Interval(1, 1));
        }

        String[] parts = string.split("/");
        PotionEffectType effectType = PotionEffectType.getByName(parts[0]);
        Interval durationBounds = parseInterval(parts[1]);
        Interval amplifierBounds;
        if (parts.length >= 3) {
            amplifierBounds = parseInterval(parts[2]);
        } else {
            amplifierBounds = new Interval(1, 1);
        }

        return RecipeEffect.of(effectType, durationBounds, amplifierBounds);
    }


}
