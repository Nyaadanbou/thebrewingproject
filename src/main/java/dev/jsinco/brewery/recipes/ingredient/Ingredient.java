package dev.jsinco.brewery.recipes.ingredient;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.jsinco.brewery.util.Logging;

import java.util.Map;

/**
 * Represents an ingredient in a recipe.
 */
public interface Ingredient<I> {

    /**
     * Check if the itemStack matches the ingredient.
     *
     * @param itemStack The itemStack to check.
     * @return True if the itemStack matches the ingredient, false otherwise.
     */
    boolean matches(I itemStack);

    String getKey();


    static <I> Map<Ingredient<I>, Integer> ingredientsFromJson(String json, IngredientManager<?> ingredientManager) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        ImmutableMap.Builder<Ingredient<I>, Integer> output = new ImmutableMap.Builder<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            ingredientManager.getIngredient(entry.getKey())
                    .ifPresentOrElse(ingredient -> output.put((Ingredient<I>) ingredient, entry.getValue().getAsInt()),
                            () -> Logging.warning("Could not find ingredient for stored brew: " + entry.getKey()));
        }
        return output.build();
    }

    static <I> String ingredientsToJson(Map<Ingredient<I>, Integer> ingredients) {
        JsonObject output = new JsonObject();
        for (Map.Entry<Ingredient<I>, Integer> entry : ingredients.entrySet()) {
            output.add(entry.getKey().getKey(), new JsonPrimitive(entry.getValue()));
        }
        return output.toString();
    }
}
