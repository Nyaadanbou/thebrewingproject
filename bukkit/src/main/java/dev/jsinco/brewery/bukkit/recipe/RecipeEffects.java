package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.bukkit.util.ListPersistentDataType;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.effect.DrunkManager;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.util.Registry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecipeEffects {

    public static final NamespacedKey COMMANDS = NamespacedKey.fromString(Registry.brewerySpacedKey("commands"));
    public static final NamespacedKey MESSAGE = NamespacedKey.fromString(Registry.brewerySpacedKey("message"));
    public static final NamespacedKey ACTION_BAR = NamespacedKey.fromString(Registry.brewerySpacedKey("action_bar"));
    public static final NamespacedKey TITLE = NamespacedKey.fromString(Registry.brewerySpacedKey("titles"));
    public static final NamespacedKey ALCOHOL = NamespacedKey.fromString(Registry.brewerySpacedKey("alcohol"));
    private static final List<NamespacedKey> PDC_TYPES = List.of(COMMANDS, MESSAGE, ACTION_BAR, TITLE, ALCOHOL);

    public static final RecipeEffects GENERIC = new Builder()
            .commands(List.of())
            .effects(List.of())
            .build();

    // Commands
    @Getter
    private final @NotNull List<String> commands;
    // Effects
    @Getter
    private final @NotNull List<@NotNull RecipeEffect> effects;
    // Messages <-- Consider removing because server owners can use commands
    @Getter
    private final @Nullable String title;
    @Getter
    private final @Nullable String message;
    @Getter
    private final @Nullable String actionBar;
    @Getter
    private final int alcohol;

    private RecipeEffects(@NotNull List<String> commands, @NotNull List<RecipeEffect> effects, @Nullable String title, @Nullable String message, @Nullable String actionBar, int alcohol) {
        this.commands = commands;
        this.effects = effects;
        this.title = title;
        this.message = message;
        this.actionBar = actionBar;
        this.alcohol = alcohol;
    }

    public void applyTo(PotionMeta meta, BrewQuality quality) {
        for (RecipeEffect recipeEffect : effects) {
            meta.addCustomEffect(recipeEffect.newPotionEffect(), true);
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PDC_TYPES.forEach(container::remove);
        container.set(COMMANDS, ListPersistentDataType.STRING_LIST_PDC_TYPE, commands);
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

    public static Optional<RecipeEffects> fromItem(@NotNull ItemStack item) {
        RecipeEffects.Builder builder = new RecipeEffects.Builder();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (!persistentDataContainer.has(ALCOHOL)) {
            return Optional.empty();
        }
        builder.commands(persistentDataContainer.getOrDefault(COMMANDS, ListPersistentDataType.STRING_LIST_PDC_TYPE, List.of()));
        builder.alcohol(persistentDataContainer.getOrDefault(ALCOHOL, PersistentDataType.INTEGER, 0));
        if (persistentDataContainer.has(TITLE)) {
            builder.title(persistentDataContainer.get(TITLE, PersistentDataType.STRING));
        }
        if (persistentDataContainer.has(MESSAGE)) {
            builder.message(persistentDataContainer.get(MESSAGE, PersistentDataType.STRING));
        }
        if (persistentDataContainer.has(ACTION_BAR)) {
            builder.actionBar(persistentDataContainer.get(ACTION_BAR, PersistentDataType.STRING));
        }
        return Optional.of(builder.build());
    }

    public void applyTo(Player player, DrunkManager drunkManager) {
        drunkManager.consume(player.getUniqueId(), alcohol, alcohol);
        this.commands.stream()
                .map(command -> compileUnparsedEffectMessage(command, player, drunkManager))
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        if (title != null) {
            player.showTitle(Title.title(MessageUtil.compilePlayerMessage(title, player, drunkManager, this.alcohol), Component.empty()));
        }
        if (message != null) {
            player.sendMessage(MessageUtil.compilePlayerMessage(message, player, drunkManager, this.alcohol));
        }
        if (actionBar != null) {
            player.sendActionBar(MessageUtil.compilePlayerMessage(actionBar, player, drunkManager, this.alcohol));
        }
    }

    private String compileUnparsedEffectMessage(String message, Player player, DrunkManager drunkManager) {
        DrunkState drunkState = drunkManager.getDrunkState(player.getUniqueId());
        return message
                .replace("<player_name>", player.getName())
                .replace("<team_name>", PlainTextComponentSerializer.plainText().serialize(player.teamDisplayName()))
                .replace("<alcohol>", String.valueOf(this.getAlcohol()))
                .replace("<player_alcohol>", String.valueOf(String.valueOf(drunkState == null ? "0" : drunkState.alcohol())))
                .replace("<world>", player.getWorld().getName());
    }

    public static class Builder {

        private List<String> commands = List.of();
        private List<RecipeEffect> effects = List.of();
        private @Nullable String title;
        private @Nullable String message;
        private @Nullable String actionBar;
        private int alcohol;

        public Builder commands(@NotNull List<String> commands) {
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
