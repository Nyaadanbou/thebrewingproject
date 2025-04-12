package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.ItemColorUtil;
import dev.jsinco.brewery.util.Pair;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.Map;

public class IngredientUtil {

    public static Pair<org.bukkit.Color, @Nullable Ingredient<ItemStack>> ingredientData(Map<? extends Ingredient<?>, Integer> ingredients) {
        int r = 0;
        int g = 0;
        int b = 0;
        int amount = 0;
        Ingredient<ItemStack> topIngredient = null;
        int topIngredientAmount = 0;
        for (Map.Entry<? extends Ingredient<?>, Integer> ingredient : ingredients.entrySet()) {
            if (topIngredientAmount < ingredient.getValue()) {
                topIngredient = (Ingredient<ItemStack>) ingredient.getKey();
                topIngredientAmount = ingredient.getValue();
            }
            String key = ingredient.getKey().getKey();
            Color color = ItemColorUtil.getItemColor(key);
            if (color == null) {
                continue;
            }
            r += color.getRed() * ingredient.getValue();
            g += color.getGreen() * ingredient.getValue();
            b += color.getBlue() * ingredient.getValue();
            amount += ingredient.getValue();
        }
        if (amount != 0) {
            return new Pair<>(org.bukkit.Color.fromRGB(r / amount, g / amount, b / amount), topIngredient);
        } else {
            return new Pair<>(org.bukkit.Color.YELLOW, topIngredient);
        }
    }
}
