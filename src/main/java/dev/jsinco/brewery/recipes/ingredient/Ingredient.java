package dev.jsinco.brewery.recipes.ingredient;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.Logging;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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



    static Map<Ingredient, Integer> ingredientsFromJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        ImmutableMap.Builder<Ingredient, Integer> output = new ImmutableMap.Builder<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            IngredientManager.getIngredient(entry.getKey())
                    .ifPresentOrElse(ingredient -> output.put(ingredient, entry.getValue().getAsInt()),
                            () -> Logging.warning("Could not find ingredient for stored brew: " + entry.getKey()));
        }
        return output.build();
    }

    static String ingredientsToJson(Map<Ingredient, Integer> ingredients) {
        JsonObject output = new JsonObject();
        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            output.add(entry.getKey().getKey().toString(), new JsonPrimitive(entry.getValue()));
        }
        return output.toString();
    }

    class PdcType implements PersistentDataType<byte[], Map<Ingredient, Integer>> {

        @NotNull
        @Override
        public Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @NotNull
        @Override
        public Class<Map<Ingredient, Integer>> getComplexType() {
            return (Class<Map<Ingredient, Integer>>) Map.of().getClass();
        }

        @Override
        public byte @NotNull [] toPrimitive(@NotNull Map<Ingredient, Integer> complex, @NotNull PersistentDataAdapterContext context) {
            byte[][] bytesArray = complex.entrySet().stream()
                    .map(entry -> entry.getKey().getKey().toString() + "/" + entry.getValue())
                    .map(string -> string.getBytes(StandardCharsets.UTF_8))
                    .toArray(byte[][]::new);
            try {
                return DecoderEncoder.encode(bytesArray);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @NotNull
        @Override
        public Map<Ingredient, Integer> fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
            Map<Ingredient, Integer> ingredients = new HashMap<>();
            byte[][] bytesArray;
            try {
                bytesArray = DecoderEncoder.decode(primitive);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Arrays.stream(bytesArray)
                    .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                    .map(IngredientManager::getIngredientWithAmount)
                    .forEach(ingredientAmountPair -> IngredientManager.insertIngredientIntoMap(ingredients, ingredientAmountPair));
            return ingredients;
        }
    }
}
