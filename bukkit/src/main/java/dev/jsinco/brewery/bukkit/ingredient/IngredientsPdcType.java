package dev.jsinco.brewery.bukkit.ingredient;

import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.DecoderEncoder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IngredientsPdcType implements PersistentDataType<byte[], Map<Ingredient<ItemStack>, Integer>> {

    public static final IngredientsPdcType INSTANCE = new IngredientsPdcType();

    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<Map<Ingredient<ItemStack>, Integer>> getComplexType() {
        return (Class<Map<Ingredient<ItemStack>, Integer>>) Map.of().getClass();
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Map<Ingredient<ItemStack>, Integer> complex, @NotNull PersistentDataAdapterContext context) {
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
    public Map<Ingredient<ItemStack>, Integer> fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        Map<Ingredient<ItemStack>, Integer> ingredients = new HashMap<>();
        byte[][] bytesArray;
        try {
            bytesArray = DecoderEncoder.decode(primitive);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Arrays.stream(bytesArray)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .map(BukkitIngredientManager.INSTANCE::getIngredientWithAmount)
                .forEach(ingredientAmountPair -> BukkitIngredientManager.INSTANCE.insertIngredientIntoMap(ingredients, ingredientAmountPair));
        return ingredients;
    }
}