package dev.jsinco.brewery.recipes.ingredient;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an ingredient in a recipe.
 */
public interface Ingredient {

    PdcType PDC_TYPE = new PdcType();

    /**
     * Check if the itemStack matches the ingredient.
     *
     * @param itemStack The itemStack to check.
     * @return True if the itemStack matches the ingredient, false otherwise.
     */
    boolean matches(ItemStack itemStack);

    NamespacedKey getKey();

    class PdcType implements PersistentDataType<String[], Map<Ingredient, Integer>> {

        @NotNull
        @Override
        public Class<String[]> getPrimitiveType() {
            return String[].class;
        }

        @NotNull
        @Override
        public Class<Map<Ingredient, Integer>> getComplexType() {
            return (Class<Map<Ingredient, Integer>>) Map.of().getClass();
        }

        @NotNull
        @Override
        public String @NotNull [] toPrimitive(@NotNull Map<Ingredient, Integer> complex, @NotNull PersistentDataAdapterContext context) {
            return complex.entrySet().stream()
                    .map(entry -> entry.getKey().getKey().toString() + "/" + entry.getValue())
                    .toArray(String[]::new);
        }

        @NotNull
        @Override
        public Map<Ingredient, Integer> fromPrimitive(@NotNull String @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
            Map<Ingredient, Integer> ingredients = new HashMap<>();
            Arrays.stream(primitive)
                    .map(IngredientManager::getIngredientWithAmount)
                    .forEach(ingredientAmountPair -> IngredientManager.insertIngredientIntoMap(ingredients, ingredientAmountPair));
            return ingredients;
        }
    }
}
