package dev.jsinco.brewery.recipes.ingredient;

import dev.jsinco.brewery.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Get an instance of an ingredient from an ItemStack or a string.
 * Used for cauldrons and loading for loading recipes.
 */
public interface IngredientManager<I> {


    Ingredient<I> getIngredient(@NotNull I itemStack);


    Optional<Ingredient<I>> getIngredient(@NotNull String ingredientStr);

    /**
     * @param ingredientStr A string with the format [ingredient-name]/[runs]. Allows not specifying runs, where it will default to 1
     * @return An ingredient/runs pair
     * @throws IllegalArgumentException if the ingredients string is invalid
     */
    Pair<@NotNull Ingredient<I>, @NotNull Integer> getIngredientWithAmount(String ingredientStr) throws IllegalArgumentException;

    void insertIngredientIntoMap(Map<Ingredient<I>, Integer> mutableIngredientsMap, Pair<Ingredient<I>, Integer> ingredient);

    /**
     * Parse a list of strings into a map of ingredients with runs
     *
     * @param stringList A list of strings with valid formatting, see {@link #getIngredientWithAmount(String)}
     * @return A map representing ingredients with runs
     * @throws IllegalArgumentException if there's any invalid ingredient string
     */
    Map<Ingredient<I>, Integer> getIngredientsWithAmount(List<String> stringList) throws IllegalArgumentException;

    default void merge(Map<Ingredient<I>, Integer> mutableIngredientsMap, Map<Ingredient<I>, Integer> ingredients) {
        for (Map.Entry<Ingredient<I>, Integer> ingredient : ingredients.entrySet()) {
            insertIngredientIntoMap(mutableIngredientsMap, new Pair<>(ingredient.getKey(), ingredient.getValue()));
        }
    }
}
