package dev.jsinco.brewery.bukkit.recipe;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.bukkit.integration.ItemsAdderWrapper;
import dev.jsinco.brewery.bukkit.integration.OraxenWrapper;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScore;
import dev.jsinco.brewery.recipes.QualityData;
import dev.jsinco.brewery.recipes.RecipeResult;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class BukkitRecipeResult implements RecipeResult<ItemStack> {

    public static final BukkitRecipeResult GENERIC = new Builder()
            .names(QualityData.equalValue("Unknown brew"))
            .lore(QualityData.equalValue(List.of()))
            .recipeEffects(QualityData.equalValue(RecipeEffects.GENERIC))
            .build();
    private final boolean glint;
    private final int customModelData;
    private final @Nullable NamespacedKey customId;

    @Getter
    private final QualityData<String> names;
    @Getter
    private final QualityData<List<String>> lore;

    @Getter
    private final QualityData<RecipeEffects> recipeEffects;
    @Getter
    private final Color color;
    private final boolean appendBrewInfoLore;

    private BukkitRecipeResult(boolean glint, int customModelData, QualityData<RecipeEffects> recipeEffects, QualityData<String> names, QualityData<List<String>> lore, Color color, boolean appendBrewInfoLore, @Nullable NamespacedKey customId) {
        this.glint = glint;
        this.customModelData = customModelData;
        this.recipeEffects = recipeEffects;
        this.names = names;
        this.lore = lore;
        this.color = color;
        this.appendBrewInfoLore = appendBrewInfoLore;
        this.customId = customId;
    }

    public ItemStack newBrewItem(@NotNull BrewScore score, Brew brew) {
        BrewQuality quality = score.brewQuality();
        if (customId != null) {
            ItemStack itemStack;
            if (customId.getNamespace().equals("oraxen")) {
                itemStack = OraxenWrapper.build(customId.getKey());
            } else if (customId.getNamespace().equals("itemsadder")) {
                itemStack = ItemsAdderWrapper.build(customId.getKey());
            } else {
                throw new IllegalStateException("Namespace should be within the supported items plugins");
            }
            if (itemStack == null) {
                throw new IllegalStateException("Unreachable code, this value should have been checked before");
            }
            ItemMeta meta = itemStack.getItemMeta();
            recipeEffects.getOrDefault(quality, RecipeEffects.GENERIC).applyTo(meta, score);
            itemStack.setItemMeta(meta);
            return itemStack;
        }
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        Preconditions.checkNotNull(quality);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.displayName(compileMessage(score, brew, names.get(quality), true).decoration(TextDecoration.ITALIC, false));
        meta.lore(Stream.concat(lore.get(quality).stream()
                                        .map(line -> compileMessage(score, brew, line, false)),
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
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private Stream<? extends Component> compileExtraLore(BrewScore score, Brew brew) {
        if (!appendBrewInfoLore) {
            return Stream.empty();
        }
        Stream.Builder<Component> streamBuilder = Stream.builder();
        streamBuilder.add(Component.empty());
        MessageUtil.compileBrewInfo(brew, score, false).forEach(streamBuilder::add);
        streamBuilder.add(compileMessage(score, brew, TranslationsConfig.BREW_TOOLTIP_QUALITY, false));
        return streamBuilder.build();
    }

    private Component compileMessage(BrewScore score, Brew brew, String serializedMiniMessage, boolean isBrewName) {
        return MiniMessage.miniMessage().deserialize(serializedMiniMessage, getResolver(score, brew, isBrewName));
    }

    private @NotNull TagResolver getResolver(BrewScore score, Brew brew, boolean isBrewName) {
        BrewQuality quality = score.brewQuality();
        TagResolver.Builder output = TagResolver.builder();
        if (!isBrewName) {
            output.resolver(Placeholder.component("brew_name", compileMessage(score, brew, this.names.get(quality), true)));
        }
        output.resolvers(
                Formatter.number("alcohol", this.getRecipeEffects().get(quality).getAlcohol()),
                MessageUtil.getScoreTagResolver(score)
        );

        return output.build();
    }

    public static class Builder {

        private boolean glint;
        private int customModelData;
        private QualityData<String> names;
        private QualityData<List<String>> lore;
        private QualityData<RecipeEffects> recipeEffects;
        private Color color = Color.BLUE;
        private boolean appendBrewInfoLore = true;
        private NamespacedKey customId;

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

        public Builder customId(@Nullable String customId) {
            if (customId == null) {
                this.customId = null;
                return this;
            }
            NamespacedKey namespacedKey = NamespacedKey.fromString(customId.toLowerCase(Locale.ROOT));
            if (namespacedKey == null) {
                throw new IllegalArgumentException("Invalid namespace!");
            }
            if (namespacedKey.getNamespace().equals("oraxen") && OraxenWrapper.isOraxen(namespacedKey.getKey())) {
                this.customId = namespacedKey;
                return this;
            }
            if (namespacedKey.getNamespace().equals("itemsadder") && ItemsAdderWrapper.isItemsAdder(namespacedKey.getKey())) {
                this.customId = namespacedKey;
                return this;
            }
            throw new IllegalArgumentException("Unknown custom namespace!");
        }

        public BukkitRecipeResult build() {
            Objects.requireNonNull(names, "Names not initialized, a recipe has to have names");
            Objects.requireNonNull(lore, "Lore not initialized, a recipe has to have lore");
            Objects.requireNonNull(recipeEffects, "Effects not initialized, a recipe has to have effects");
            return new BukkitRecipeResult(glint, customModelData, recipeEffects, names, lore, color, appendBrewInfoLore, customId);
        }

        public Builder name(String name) {
            this.names = QualityData.equalValue(name);
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = QualityData.equalValue(lore);
            return this;
        }
    }
}
