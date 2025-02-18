package dev.jsinco.brewery.recipes.ingredient;

import dev.jsinco.brewery.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Get an instance of an ingredient from an ItemStack or a string.
 * Used for cauldrons and loading for loading recipes.
 */
public interface IngredientManager<I> {


    Ingredient<I> getIngredient(@NotNull I itemStack);


    Optional<Ingredient<I>> getIngredient(@NotNull String ingredientStr);

    /**
     * @param ingredientStr A string with the format [ingredient-name]/[amount]. Allows not specifying amount, where it will default to 1
     * @return An ingredient/amount pair
     * @throws IllegalArgumentException if the ingredients string is invalid
     */
    Pair<@NotNull Ingredient<I>, @NotNull Integer> getIngredientWithAmount(String ingredientStr) throws IllegalArgumentException;

    void insertIngredientIntoMap(Map<Ingredient<I>, Integer> mutableIngredientsMap, Pair<Ingredient<I>, Integer> ingredient);

    /**
     * Parse a list of strings into a map of ingredients with amount
     *
     * @param stringList A list of strings with valid formatting, see {@link #getIngredientWithAmount(String)}
     * @return A map representing ingredients with amount
     * @throws IllegalArgumentException if there's any invalid ingredient string
     */
    Map<Ingredient<I>, Integer> getIngredientsWithAmount(List<String> stringList) throws IllegalArgumentException;
}
