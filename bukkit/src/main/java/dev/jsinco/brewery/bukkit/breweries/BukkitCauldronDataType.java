package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Cauldron;
import dev.jsinco.brewery.breweries.CauldronDataType;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BukkitCauldronDataType extends CauldronDataType<ItemStack> {
    public static final BukkitCauldronDataType INSTANCE = new BukkitCauldronDataType();

    @Override
    protected IngredientManager<ItemStack> getIngredientManager() {
        return BukkitIngredientManager.INSTANCE;
    }

    @Override
    protected Cauldron<ItemStack> newCauldron(BreweryLocation location, Map<Ingredient<ItemStack>, Integer> ingredients, long brewStart) {
        return new BukkitCauldron(ingredients, Bukkit.getWorld(location.worldUuid()).getBlockAt(location.x(), location.y(), location.z()), brewStart);
    }
}
