package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.integration.ItemsAdderWrapper;
import dev.jsinco.brewery.bukkit.integration.OraxenWrapper;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemsAdderPluginIngredient implements Ingredient {

    private final String itemId;

    private ItemsAdderPluginIngredient(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getKey() {
        return "itemsadder:" + itemId;
    }

    @Override
    public String displayName() {
        return ItemsAdderWrapper.displayName(itemId);
    }

    public static Optional<Ingredient> from(String itemsAdderId) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(itemsAdderId);
        if (namespacedKey == null || !namespacedKey.getNamespace().equals("itemsadder")) {
            return Optional.empty();
        }
        return Optional.of(new ItemsAdderPluginIngredient(namespacedKey.getKey()));
    }

    public static Optional<Ingredient> from(ItemStack itemStack) {
        return Optional.ofNullable(ItemsAdderWrapper.itemsAdderId(itemStack))
                .map(ItemsAdderPluginIngredient::new);
    }
}
