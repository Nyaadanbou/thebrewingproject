package dev.jsinco.brewery.bukkit.ingredient.external;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.bukkit.integration.item.OraxenHook;
import dev.jsinco.brewery.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

// PluginIngredient usage example using Oraxen
public class OraxenPluginIngredient implements Ingredient {

    private final String itemId;

    private OraxenPluginIngredient(@NotNull String itemId) {
        Preconditions.checkNotNull(itemId, "itemId cannot be null");
        this.itemId = itemId;
    }

    @Override
    public String getKey() {
        return "oraxen:" + itemId;
    }

    @Override
    public String displayName() {
        return OraxenHook.displayName(itemId);
    }

    public static Optional<Ingredient> from(String oraxenId) {
        BreweryKey namespacedKey = BreweryKey.parse(oraxenId);
        if (!namespacedKey.namespace().equals("oraxen")) {
            return Optional.empty();
        }
        return Optional.of(new OraxenPluginIngredient(namespacedKey.key()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OraxenPluginIngredient that = (OraxenPluginIngredient) o;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemId);
    }

    public static Optional<Ingredient> from(ItemStack itemStack) {
        return Optional.ofNullable(OraxenHook.oraxenId(itemStack))
                .map(OraxenPluginIngredient::new);
    }
}
