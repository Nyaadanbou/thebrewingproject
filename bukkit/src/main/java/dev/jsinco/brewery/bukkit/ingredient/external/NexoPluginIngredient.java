package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.integration.item.NexoHook;
import dev.jsinco.brewery.ingredient.Ingredient;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class NexoPluginIngredient implements Ingredient {

    private final String nexoId;

    private NexoPluginIngredient(String nexoId) {
        this.nexoId = nexoId;
    }

    @Override
    public String getKey() {
        return "nexo:" + nexoId;
    }

    @Override
    public String displayName() {
        return NexoHook.displayName(nexoId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexoPluginIngredient that = (NexoPluginIngredient) o;
        return Objects.equals(nexoId, that.nexoId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nexoId);
    }

    public static Optional<Ingredient> from(String nexoId) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(nexoId);
        if (namespacedKey == null || !namespacedKey.getNamespace().equals("nexo")) {
            return Optional.empty();
        }
        return Optional.of(new NexoPluginIngredient(namespacedKey.getKey()));
    }

    public static Optional<Ingredient> from(ItemStack itemStack) {
        return Optional.ofNullable(NexoHook.nexoId(itemStack))
                .map(NexoPluginIngredient::new);
    }
}
