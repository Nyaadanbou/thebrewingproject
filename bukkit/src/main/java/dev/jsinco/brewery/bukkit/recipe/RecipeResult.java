package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.recipes.PotionQuality;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeResult {

    public static final RecipeResult GENERIC = new Builder()
            .names(Map.of(
                    PotionQuality.EXCELLENT, "excellent",
                    PotionQuality.GOOD, "good",
                    PotionQuality.BAD, "bad"
            ))
            .lore(Map.of(
                    PotionQuality.EXCELLENT, List.of(),
                    PotionQuality.GOOD, List.of(),
                    PotionQuality.BAD, List.of()
            ))
            .recipeEffects(RecipeEffects.GENERIC)
            .build();
    private final boolean glint;
    private final int customModelData;
    // Potion attributes
    private final Map<PotionQuality, String> names;
    private final Map<PotionQuality, List<String>> lore;

    private final RecipeEffects recipeEffects;
    @Getter
    private final Color color;

    private RecipeResult(boolean glint, int customModelData, RecipeEffects recipeEffects, Map<PotionQuality, String> names, Map<PotionQuality, List<String>> lore, Color color) {
        this.glint = glint;
        this.customModelData = customModelData;
        this.recipeEffects = recipeEffects;
        this.names = names;
        this.lore = lore;
        this.color = color;
    }

    public ItemStack newBrewItem(@NotNull PotionQuality quality) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        applyMeta(quality, meta);
        item.setItemMeta(meta);
        return item;
    }

    public void applyMeta(PotionQuality quality, PotionMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(names.get(quality));
        meta.setLore(lore.get(quality));
        meta.setColor(color);
        if (glint) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        recipeEffects.applyTo(meta, quality);
    }

    public static class Builder {

        private boolean glint;
        private int customModelData;
        private Map<PotionQuality, String> names;
        private Map<PotionQuality, List<String>> lore;
        private RecipeEffects recipeEffects;
        private Color color = Color.BLUE;

        public Builder glint(boolean glint) {
            this.glint = glint;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder names(@NotNull Map<PotionQuality, String> names) {
            this.names = Objects.requireNonNull(names);
            return this;
        }

        public Builder lore(@NotNull Map<PotionQuality, List<String>> lore) {
            this.lore = Objects.requireNonNull(lore);
            return this;
        }

        public Builder recipeEffects(@NotNull RecipeEffects recipeEffects) {
            this.recipeEffects = Objects.requireNonNull(recipeEffects);
            return this;
        }

        public Builder color(@NotNull Color color) {
            this.color = color;
            return this;
        }

        public RecipeResult build() {
            Objects.requireNonNull(names, "Names not initialized, a recipe has to have names");
            Objects.requireNonNull(lore, "Lore not initialized, a recipe has to have lore");
            Objects.requireNonNull(recipeEffects, "Effects not initialized, a recipe has to have effects");
            return new RecipeResult(glint, customModelData, recipeEffects, names, lore, color);
        }
    }
}
