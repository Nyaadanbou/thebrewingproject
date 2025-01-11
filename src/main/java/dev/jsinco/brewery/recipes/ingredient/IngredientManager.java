package dev.jsinco.brewery.recipes.ingredient;

import dev.jsinco.brewery.util.Logging;
import dev.jsinco.brewery.util.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Get an instance of an ingredient from an ItemStack or a string.
 * Used for cauldrons and loading for loading recipes.
 */
public class IngredientManager {


    public static Ingredient getIngredient(@NotNull ItemStack itemStack) {
        Ingredient ingredient = PluginIngredient.of(itemStack);
        if (ingredient == null) {
            ingredient = SimpleIngredient.of(itemStack);
        }
        return ingredient;
    }


    public static Optional<Ingredient> getIngredient(@NotNull String ingredientStr) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(ingredientStr.toLowerCase(Locale.ROOT));
        if (namespacedKey != null && !NamespacedKey.MINECRAFT.equals(namespacedKey.getNamespace()) && !NamespacedKey.BUKKIT.equals(namespacedKey.getNamespace())) {
            String[] p2 = ingredientStr.split(":");
            String type = p2[0];
            String itemId = p2[1];
            return PluginIngredient.of(type, itemId)
                    .map(Ingredient.class::cast);
        } else {
            return SimpleIngredient.of(ingredientStr)
                    .map(Ingredient.class::cast);
        }
    }

    /**
     * @param ingredientStr A string with the format [ingredient-name]/[amount]. Allows not specifying amount, where it will default to 1
     * @return An ingredient/amount pair
     * @throws IllegalArgumentException if the ingredients string is invalid
     */
    public static Pair<@NotNull Ingredient, @NotNull Integer> getIngredientWithAmount(String ingredientStr) throws IllegalArgumentException {
        String[] ingredientSplit = ingredientStr.split("/");
        if (ingredientSplit.length > 2) {
            throw new IllegalArgumentException("To many '/' separators for ingredientString, was: " + ingredientStr);
        }
        int amount;
        if (ingredientSplit.length == 1) {
            amount = 1;
        } else {
            amount = Integer.parseInt(ingredientSplit[1]);
        }
        return getIngredient(ingredientSplit[0])
                .map(ingredient -> new Pair<>(ingredient, amount))
                .orElseThrow(() -> new IllegalArgumentException("Invalid ingredient string '" + ingredientStr + "' could not parse type"));
    }

    public static void insertIngredientIntoMap(Map<Ingredient, Integer> mutableIngredientsMap, Pair<Ingredient, Integer> ingredient) {
        int amount = mutableIngredientsMap.computeIfAbsent(ingredient.first(), ignored -> 0);
        mutableIngredientsMap.put(ingredient.first(), amount + ingredient.second());
    }

    /**
     * Parse a list of strings into a map of ingredients with amount
     *
     * @param stringList A list of strings with valid formatting, see {@link IngredientManager#getIngredientWithAmount(String)}
     * @return A map representing ingredients with amount
     * @throws IllegalArgumentException if there's any invalid ingredient string
     */
    public static Map<Ingredient, Integer> getIngredientsWithAmount(List<String> stringList) throws IllegalArgumentException {
        if (stringList == null || stringList.isEmpty()) {
            return new HashMap<>();
        }
        Map<Ingredient, Integer> ingredientMap = new HashMap<>();
        stringList.stream()
                .map(IngredientManager::getIngredientWithAmount)
                .forEach(ingredientAmountPair -> insertIngredientIntoMap(ingredientMap, ingredientAmountPair));
        return ingredientMap;
    }
}
