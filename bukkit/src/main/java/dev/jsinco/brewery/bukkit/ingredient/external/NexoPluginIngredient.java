package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.integration.NexoWrapper;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class NexoPluginIngredient implements Ingredient {

    private final String nexoId;

    private NexoPluginIngredient(String nexoId) {
        this.nexoId = nexoId;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return nexoId.equals(NexoWrapper.nexoId(itemStack));
    }

    @Override
    public String getKey() {
        return "nexo:" + nexoId;
    }

    @Override
    public String displayName() {
        return NexoWrapper.displayName(nexoId);
    }

    public static Optional<Ingredient> from(String nexoId) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(nexoId);
        if (namespacedKey == null || !namespacedKey.getNamespace().equals("nexo")) {
            return Optional.empty();
        }
        return Optional.of(new NexoPluginIngredient(namespacedKey.getKey()));
    }

    public static Optional<Ingredient> from(ItemStack itemStack) {
        return Optional.ofNullable(NexoWrapper.nexoId(itemStack))
                .map(NexoPluginIngredient::new);
    }
}
