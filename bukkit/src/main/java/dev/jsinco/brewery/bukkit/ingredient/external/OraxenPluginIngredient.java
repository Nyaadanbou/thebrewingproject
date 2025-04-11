package dev.jsinco.brewery.bukkit.ingredient.external;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.bukkit.integration.OraxenWrapper;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

// PluginIngredient usage example using Oraxen
public class OraxenPluginIngredient implements Ingredient<ItemStack> {

    private final String itemId;

    private OraxenPluginIngredient(@NotNull String itemId) {
        Preconditions.checkNotNull(itemId, "itemId cannot be null");
        this.itemId = itemId;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return this.itemId.equals(OraxenWrapper.oraxenId(itemStack));
    }

    @Override
    public String getKey() {
        return "oraxen:" + itemId;
    }

    @Override
    public String displayName() {
        return OraxenWrapper.displayName(itemId);
    }

    public static Optional<Ingredient<ItemStack>> from(String oraxenId) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(oraxenId);
        if (namespacedKey == null || !namespacedKey.getNamespace().equals("oraxen") || !OraxenWrapper.isOraxen(oraxenId)) {
            return Optional.empty();
        }
        return Optional.of(new OraxenPluginIngredient(namespacedKey.getKey()));
    }

    public static Optional<Ingredient<ItemStack>> from(ItemStack itemStack) {
        return Optional.ofNullable(OraxenWrapper.oraxenId(itemStack))
                .map(OraxenPluginIngredient::new);
    }
}
