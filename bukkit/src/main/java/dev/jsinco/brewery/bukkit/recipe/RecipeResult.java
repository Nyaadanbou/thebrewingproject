package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.recipes.PotionQuality;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.function.Supplier;

public class RecipeResult {

    public static final RecipeResult GENERIC = new Builder()
            .names(Map.of(
                    PotionQuality.EXCELLENT, "Unknown brew",
                    PotionQuality.GOOD, "Unknown brew",
                    PotionQuality.BAD, "Unknown brew"
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

    @Getter
    private final Map<PotionQuality, String> names;
    @Getter
    private final Map<PotionQuality, List<String>> lore;

    @Getter
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
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setHideTooltip(true);
        meta.displayName(compileMessage(quality, names.get(quality), "brew_name"));
        meta.lore(lore.get(quality).stream()
                .map(line -> compileMessage(quality, line))
                .toList()
        );
        meta.setColor(color);
        if (glint) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        recipeEffects.applyTo(meta, quality);
    }

    private Component compileMessage(PotionQuality potionQuality, String serializedMiniMessage, String... banned) {
        Map<String, Supplier<Component>> resolverMap = Map.of(
                "brew_name", () -> compileMessage(potionQuality, this.names.get(potionQuality), "brew_name"),
                "alcohol", () -> Component.text(String.valueOf(this.getRecipeEffects().getAlcohol())),
                "quality", () -> Component.text(potionQuality.toString()));
        TagResolver[] placeholders = resolverMap.entrySet().stream()
                .filter(entry -> !ArrayUtils.contains(banned, entry.getKey()))
                .map(entry -> Placeholder.component(entry.getKey(), entry.getValue().get()))
                .toArray(TagResolver[]::new);
        return MiniMessage.miniMessage().deserialize(serializedMiniMessage, placeholders);
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
