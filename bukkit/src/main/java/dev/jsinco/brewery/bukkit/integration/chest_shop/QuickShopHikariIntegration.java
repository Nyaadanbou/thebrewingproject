package dev.jsinco.brewery.bukkit.integration.chest_shop;

import com.ghostchu.quickshop.api.event.general.ShopItemMatchEvent;
import dev.jsinco.brewery.api.ingredient.Ingredient;
import dev.jsinco.brewery.api.ingredient.ScoredIngredient;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.api.integration.ChestShopIntegration;
import dev.jsinco.brewery.bukkit.ingredient.BreweryIngredient;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.util.ClassUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class QuickShopHikariIntegration implements ChestShopIntegration, Listener {
    @Override
    public String getId() {
        return "quick_shop_hikari";
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, TheBrewingProject.getInstance());
    }

    @Override
    public boolean isEnabled() {
        return ClassUtil.exists("com.ghostchu.quickshop.api.event.general.ShopItemMatchEvent");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopItemMatch(ShopItemMatchEvent event) {
        event.matches(matches(event.original(), event.comparison()));
    }

    private boolean matches(ItemStack originalItem, ItemStack comparisonItem) {
        Ingredient original = BukkitIngredientManager.INSTANCE.getIngredient(originalItem);
        double originalScore = 1D;
        if (original instanceof ScoredIngredient(Ingredient baseIngredient, double score1)) {
            originalScore = score1;
            original = baseIngredient;
        }
        if (!(original instanceof BreweryIngredient origininalBreweryIngredient)) {
            return false;
        }
        Ingredient comparison = BukkitIngredientManager.INSTANCE.getIngredient(comparisonItem);
        double comparisonScore = 1D;
        if (comparison instanceof ScoredIngredient(Ingredient baseIngredient, double score1)) {
            comparisonScore = score1;
            comparison = baseIngredient;
        }
        if (!(comparison instanceof BreweryIngredient comparisonBreweryIngredient)) {
            return false;
        }
        return origininalBreweryIngredient.equals(comparisonBreweryIngredient) && comparisonScore > originalScore;
    }
}
