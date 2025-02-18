package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.brews.BarrelBrewDataType;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import org.bukkit.inventory.ItemStack;

public class BukkitBarrelBrewDataType extends BarrelBrewDataType<ItemStack> {
    public static final BukkitBarrelBrewDataType DATA_TYPE = new BukkitBarrelBrewDataType();

    @Override
    protected IngredientManager<ItemStack> getIngredientManager() {
        return BukkitIngredientManager.INSTANCE;
    }
}
