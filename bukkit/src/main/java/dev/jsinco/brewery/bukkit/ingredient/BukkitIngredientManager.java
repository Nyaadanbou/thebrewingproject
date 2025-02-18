package dev.jsinco.brewery.bukkit.ingredient;

import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BukkitIngredientManager implements IngredientManager<ItemStack> {

    public static final BukkitIngredientManager INSTANCE = new BukkitIngredientManager();

    public Ingredient<ItemStack> getIngredient(@NotNull ItemStack itemStack) {
        Ingredient<ItemStack> ingredient = PluginIngredient.of(itemStack);
        if (ingredient == null) {
            ingredient = SimpleIngredient.of(itemStack);
        }
        return ingredient;
    }


    public Optional<Ingredient<ItemStack>> getIngredient(@NotNull String ingredientStr) {
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
    public Pair<@NotNull Ingredient<ItemStack>, @NotNull Integer> getIngredientWithAmount(String ingredientStr) throws IllegalArgumentException {
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

    public void insertIngredientIntoMap(Map<Ingredient<ItemStack>, Integer> mutableIngredientsMap, Pair<Ingredient<ItemStack>, Integer> ingredient) {
        int amount = mutableIngredientsMap.computeIfAbsent(ingredient.first(), ignored -> 0);
        mutableIngredientsMap.put(ingredient.first(), amount + ingredient.second());
    }

    /**
     * Parse a list of strings into a map of ingredients with amount
     *
     * @param stringList A list of strings with valid formatting, see {@link #getIngredientWithAmount(String)}
     * @return A map representing ingredients with amount
     * @throws IllegalArgumentException if there's any invalid ingredient string
     */
    public Map<Ingredient<ItemStack>, Integer> getIngredientsWithAmount(List<String> stringList) throws IllegalArgumentException {
        if (stringList == null || stringList.isEmpty()) {
            return new HashMap<>();
        }
        Map<Ingredient<ItemStack>, Integer> ingredientMap = new HashMap<>();
        stringList.stream()
                .map(this::getIngredientWithAmount)
                .forEach(ingredientAmountPair -> insertIngredientIntoMap(ingredientMap, ingredientAmountPair));
        return ingredientMap;
    }

}
