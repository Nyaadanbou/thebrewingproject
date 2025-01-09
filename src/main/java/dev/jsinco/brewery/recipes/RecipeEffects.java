package dev.jsinco.brewery.recipes;

import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.pdc.ListPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeEffects {

    public static final NamespacedKey COMMANDS = Registry.brewerySpacedKey("commands");
    public static final NamespacedKey MESSAGE = Registry.brewerySpacedKey("message");
    public static final NamespacedKey ACTION_BAR = Registry.brewerySpacedKey("action_bar");
    public static final NamespacedKey TITLE = Registry.brewerySpacedKey("titles");
    public static final NamespacedKey ALCOHOL = Registry.brewerySpacedKey("alcohol");

    // Commands
    private final @NotNull Map<PotionQuality, List<String>> commands;
    // Effects
    private final @NotNull List<@NotNull RecipeEffect> effects;
    // Messages <-- Consider removing because server owners can use commands
    private final @Nullable String title;
    private final @Nullable String message;
    private final @Nullable String actionBar;
    private final int alcohol;

    private RecipeEffects(@NotNull Map<PotionQuality, List<String>> commands, @NotNull List<RecipeEffect> effects, @Nullable String title, @Nullable String message, @Nullable String actionBar, int alcohol) {
        this.commands = commands;
        this.effects = effects;
        this.title = title;
        this.message = message;
        this.actionBar = actionBar;
        this.alcohol = alcohol;
    }

    public void applyTo(PotionMeta meta, PotionQuality quality) {
        for (RecipeEffect recipeEffect : effects) {
            meta.addCustomEffect(recipeEffect.getPotionEffect(quality), true);
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(COMMANDS, ListPersistentDataType.STRING_LIST_PDC_TYPE, commands.get(quality));
        if (title != null) {
            container.set(TITLE, PersistentDataType.STRING, title);
        }
        if (message != null) {
            container.set(MESSAGE, PersistentDataType.STRING, message);
        }
        if (actionBar != null) {
            container.set(ACTION_BAR, PersistentDataType.STRING, actionBar);
        }
        container.set(ALCOHOL, PersistentDataType.INTEGER, alcohol);
    }

    public static class Builder {

        private Map<PotionQuality, List<String>> commands = Map.of();
        private List<RecipeEffect> effects = List.of();
        private @Nullable String title;
        private @Nullable String message;
        private @Nullable String actionBar;
        private int alcohol;

        public Builder commands(@NotNull Map<PotionQuality, List<String>> commands) {
            this.commands = commands;
            return this;
        }

        public Builder effects(@NotNull List<@NotNull RecipeEffect> effects) {
            this.effects = effects;
            return this;
        }

        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder message(@Nullable String message) {
            this.message = message;
            return this;
        }

        public Builder actionBar(@Nullable String actionBar) {
            this.actionBar = actionBar;
            return this;
        }

        public Builder alcohol(int alcohol) {
            this.alcohol = alcohol;
            return this;
        }

        public RecipeEffects build() {
            Objects.requireNonNull(commands);
            Objects.requireNonNull(effects);
            return new RecipeEffects(commands, effects, title, message, actionBar, alcohol);
        }

    }
}
