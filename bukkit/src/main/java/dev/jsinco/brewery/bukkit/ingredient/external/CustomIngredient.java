package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.ingredient.PluginIngredient;
import org.bukkit.inventory.ItemStack;

public class CustomIngredient extends PluginIngredient {

    // TODO: handle this tomorrow

    @Override
    public String getItemIdByItemStack(ItemStack itemStack) {
        return "";
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return false;
    }

    @Override
    public String displayName() {
        return "";
    }
}
