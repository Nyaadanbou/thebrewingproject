package dev.jsinco.brewery.bukkit.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScore;
import dev.jsinco.brewery.recipes.QualityData;
import dev.jsinco.brewery.recipes.RecipeResult;
import dev.jsinco.brewery.util.Logging;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
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
import java.util.stream.Stream;

public class BukkitRecipeResult implements RecipeResult<ItemStack, PotionMeta> {

    public static final BukkitRecipeResult GENERIC = new Builder()
            .names(QualityData.equalValue("Unknown brew"))
            .lore(QualityData.equalValue(List.of()))
            .recipeEffects(QualityData.equalValue(RecipeEffects.GENERIC))
            .build();
    private final boolean glint;
    private final int customModelData;

    @Getter
    private final QualityData<String> names;
    @Getter
    private final QualityData<List<String>> lore;

    @Getter
    private final QualityData<RecipeEffects> recipeEffects;
    @Getter
    private final Color color;
    private final boolean appendBrewInfoLore;

    private BukkitRecipeResult(boolean glint, int customModelData, QualityData<RecipeEffects> recipeEffects, QualityData<String> names, QualityData<List<String>> lore, Color color, boolean appendBrewInfoLore) {
        this.glint = glint;
        this.customModelData = customModelData;
        this.recipeEffects = recipeEffects;
        this.names = names;
        this.lore = lore;
        this.color = color;
        this.appendBrewInfoLore = appendBrewInfoLore;
    }

    public ItemStack newBrewItem(@NotNull BrewScore score, Brew<ItemStack> brew) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        applyMeta(score, meta, brew);
        item.setItemMeta(meta);
        return item;
    }

    public void applyMeta(BrewScore score, PotionMeta meta, Brew<ItemStack> brew) {
        //TODO translations
        BrewQuality quality = score.brewQuality();
        Preconditions.checkNotNull(quality);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.displayName(compileMessage(score, brew, names.get(quality), "brew_name").decoration(TextDecoration.ITALIC, false));
        meta.lore(Stream.concat(lore.get(quality).stream()
                                        .map(line -> compileMessage(score, brew, line)),
                                compileExtraLore(score, brew)
                        )
                        .map(component -> component.decoration(TextDecoration.ITALIC, false))
                        .map(component -> component.colorIfAbsent(NamedTextColor.GRAY))
                        .toList()
        );
        meta.setColor(color);
        if (glint) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        recipeEffects.get(quality).applyTo(meta);
    }

    private Stream<? extends Component> compileExtraLore(BrewScore score, Brew<ItemStack> brew) {
        if (!appendBrewInfoLore) {
            return Stream.empty();
        }
        Stream.Builder<Component> streamBuilder = Stream.builder();
        streamBuilder.add(Component.empty());
        streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_INGREDIENTS));
        if (brew.aging() != null) {
            streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_AGING));
        }
        if (brew.distillRuns() > 0) {
            streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_DISTILLING));
        }
        streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_COOKING));
        streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_QUALITY));
        return streamBuilder.build();
    }

    private Component compileMessage(BrewScore score, Brew<ItemStack> brew, String serializedMiniMessage, String... banned) {
        Map<String, Supplier<TagResolver>> resolverMap = getResolverMap(score, brew);
        TagResolver[] placeholders = resolverMap.entrySet().stream()
                .filter(entry -> !ArrayUtils.contains(banned, entry.getKey()))
                .map(entry -> entry.getValue().get())
                .toArray(TagResolver[]::new);
        return MiniMessage.miniMessage().deserialize(serializedMiniMessage, placeholders);
    }

    private @NotNull Map<String, Supplier<TagResolver>> getResolverMap(BrewScore score, Brew<ItemStack> brew) {
        BrewQuality quality = score.brewQuality();
        ImmutableMap.Builder<String, Supplier<TagResolver>> resolverMap = ImmutableMap.builder();
        resolverMap.put("brew_name", () -> Placeholder.component("brew_name", compileMessage(score, brew, this.names.get(quality), "brew_name")));
        resolverMap.put("alcohol", () -> Formatter.number("alcohol", this.getRecipeEffects().get(quality).getAlcohol()));
        resolverMap.put("quality", () -> Placeholder.component("quality", Component.text(score.displayName())));
        resolverMap.put("aging_years", () -> Formatter.number("aging_years", brew.aging() != null ? brew.aging().agingYears() : 0));
        resolverMap.put("distill_amount", () -> Formatter.number("distill_amount", brew.distillRuns()));
        resolverMap.put("cooking_time", () -> Formatter.number("cooking_time", brew.brewTime().minutes()));
        resolverMap.put("aging_quality_color", () -> Placeholder.styling("aging_quality_color", TextColor.color(BrewScore.quality(score.agingTimeScore() * score.barrelTypeScore()).getColor())));
        resolverMap.put("distilling_quality_color", () -> Placeholder.styling("distilling_quality_color", TextColor.color(TextColor.color(BrewScore.quality(score.distillRunsScore()).getColor()))));
        resolverMap.put("cooking_quality_color", () -> Placeholder.styling("cooking_quality_color", TextColor.color(BrewScore.quality(score.cauldronTypeScore() * score.cauldronTimeScore()).getColor())));
        resolverMap.put("quality_color", () -> Placeholder.styling("quality_color", TextColor.color(quality.getColor())));
        resolverMap.put("ingredients_quality_color", () -> Placeholder.styling("ingredients_quality_color", TextColor.color(BrewScore.quality(score.ingredientScore()).getColor())));
        return resolverMap.build();
    }

    public static class Builder {

        private boolean glint;
        private int customModelData;
        private QualityData<String> names;
        private QualityData<List<String>> lore;
        private QualityData<RecipeEffects> recipeEffects;
        private Color color = Color.BLUE;
        private boolean appendBrewInfoLore = true;

        public Builder glint(boolean glint) {
            this.glint = glint;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder names(@NotNull QualityData<String> names) {
            this.names = Objects.requireNonNull(names);
            return this;
        }

        public Builder lore(@NotNull QualityData<List<String>> lore) {
            this.lore = Objects.requireNonNull(lore);
            return this;
        }

        public Builder recipeEffects(@NotNull QualityData<RecipeEffects> recipeEffects) {
            this.recipeEffects = Objects.requireNonNull(recipeEffects);
            return this;
        }

        public Builder color(@NotNull Color color) {
            this.color = color;
            return this;
        }

        public Builder appendBrewInfoLore(boolean appendBrewInfoLore) {
            this.appendBrewInfoLore = appendBrewInfoLore;
            return this;
        }

        public BukkitRecipeResult build() {
            Objects.requireNonNull(names, "Names not initialized, a recipe has to have names");
            Objects.requireNonNull(lore, "Lore not initialized, a recipe has to have lore");
            Objects.requireNonNull(recipeEffects, "Effects not initialized, a recipe has to have effects");
            return new BukkitRecipeResult(glint, customModelData, recipeEffects, names, lore, color, appendBrewInfoLore);
        }

        public Builder name(String name) {
            this.names = QualityData.equalValue(name);
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = QualityData.equalValue(lore);
            return this;
        }

        private <T> Map<BrewQuality, T> equalQualityResultMap(T t) {
            return Map.of(
                    BrewQuality.EXCELLENT, t,
                    BrewQuality.GOOD, t,
                    BrewQuality.BAD, t
            );
        }
    }
}
