package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.ingredient.PluginIngredient;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

// PluginIngredient usage example using Oraxen
public class OraxenPluginIngredient extends PluginIngredient {
    @Override
    public boolean matches(ItemStack itemStack) {
        String itemId = this.getItemIdByItemStack(itemStack);
        if (itemId == null) {
            return false;
        }
        return itemId.equals(this.getItemId());
    }

    @Override
    public String displayName() {
        return OraxenItems.getItemById(this.getItemId()).getDisplayName();
    }

    @Override
    public String getItemIdByItemStack(ItemStack itemStack) {
        return OraxenItems.getIdByItem(itemStack);
    }
}
