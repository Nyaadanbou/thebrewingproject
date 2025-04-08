package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.recipe.RecipeEffects;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.effect.DrunksManager;
import dev.jsinco.brewery.effect.event.DrunkEvent;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScore;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class MessageUtil {

    public static Component compilePlayerMessage(String message, Player player, DrunksManager drunksManager, int alcohol) {
        DrunkState drunkState = drunksManager.getDrunkState(player.getUniqueId());
        return MiniMessage.miniMessage().deserialize(
                message,
                Placeholder.parsed("alcohol", String.valueOf(alcohol)),
                Placeholder.parsed("player_alcohol", String.valueOf(drunkState == null ? "0" : drunkState.alcohol())),
                getPlayerTagResolver(player)
        );
    }

    public static TagResolver getPlayerTagResolver(Player player) {
        return TagResolver.resolver(
                Placeholder.component("player_name", player.name()),
                Placeholder.component("team_name", player.teamDisplayName()),
                Placeholder.unparsed("world", player.getWorld().getName())
        );
    }

    public static TagResolver getBrewTagResolver(@NotNull Brew<ItemStack> brew) {
        return TagResolver.resolver(
                Formatter.number("aging_years", brew.aging() != null ? brew.aging().agingYears() : 0),
                Formatter.number("distill_amount", brew.distillRuns()),
                Formatter.number("cooking_time", brew.brewTime() != null ? brew.brewTime().minutes() : 0),
                Placeholder.parsed("ingredients", formatIngredients(brew.ingredients())),
                Placeholder.parsed("barrel_type", brew.barrelType() != null ? brew.barrelType().translation() : TranslationsConfig.BARREL_TYPE_NONE),
                Placeholder.parsed("cauldron_type", brew.cauldronType() != null ? brew.cauldronType().translation() : TranslationsConfig.CAULDRON_TYPE_NONE)
        );
    }

    public static TagResolver getScoreTagResolver(@NotNull BrewScore score) {
        BrewQuality quality = score.brewQuality();
        BrewQuality agingQuality = BrewScore.quality(score.agingTimeScore() * score.barrelTypeScore());
        BrewQuality distillingQuality = BrewScore.quality(score.distillRunsScore());
        BrewQuality cookingQuality = BrewScore.quality(score.cauldronTypeScore() * score.cauldronTimeScore());
        BrewQuality ingredientsQuality = BrewScore.quality(score.ingredientScore());
        return TagResolver.resolver(
                Placeholder.component("quality", Component.text(score.displayName())),
                Placeholder.styling("aging_quality_color", resolveQualityColor(agingQuality)),
                Placeholder.styling("distilling_quality_color", resolveQualityColor(distillingQuality)),
                Placeholder.styling("cooking_quality_color", resolveQualityColor(cookingQuality)),
                Placeholder.styling("quality_color", resolveQualityColor(quality)),
                Placeholder.styling("ingredients_quality_color", resolveQualityColor(ingredientsQuality))
        );
    }

    private static @NotNull StyleBuilderApplicable[] resolveQualityColor(@Nullable BrewQuality quality) {
        return quality != null ? new StyleBuilderApplicable[]{TextColor.color(quality.getColor())} : new StyleBuilderApplicable[]{NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH};
    }

    public static TagResolver recipeTagResolver(Recipe<ItemStack, PotionMeta> recipe) {
        return TagResolver.resolver(
                Placeholder.parsed("recipe_name", recipe.getRecipeName()),
                Formatter.number("recipe_brew_time", recipe.getBrewTime()),
                Placeholder.parsed("recipe_ingredients", formatIngredients(recipe.getIngredients())),
                Formatter.number("recipe_difficulty", recipe.getBrewDifficulty()),
                Placeholder.parsed("recipe_barrel_type", recipe.getBarrelType().translation()),
                Placeholder.parsed("recipe_cauldron_type", recipe.getCauldronType().translation()),
                Formatter.number("recipe_aging_years", recipe.getAgingYears()),
                Formatter.number("recipe_distill_runs", recipe.getDistillRuns()),
                Formatter.number("distill_time", recipe.getDistillTime())
        );
    }

    public static TagResolver recipeEffectResolver(RecipeEffects effects) {
        return TagResolver.resolver(
                Placeholder.component("potion_effects", effects.getEffects().stream()
                        .map(effect ->
                                Component.translatable(effect.type().translationKey())
                                        .append(Component.text("/" + effect.durationRange() + "/" + effect.amplifierRange()))
                        ).collect(Component.toComponent(Component.text(",")))
                ),
                Formatter.number("effect_alcohol", effects.getAlcohol()),
                Formatter.number("effect_toxins", effects.getToxins()),
                Placeholder.parsed("effect_title_message", effects.getTitle() == null ? "" : effects.getTitle()),
                Placeholder.parsed("effect_message", effects.getMessage() == null ? "" : effects.getMessage()),
                Placeholder.parsed("effect_action_bar", effects.getActionBar() == null ? "" : effects.getActionBar()),
                Placeholder.parsed("effect_events", effects.getEvents().stream().map(DrunkEvent::getTranslation).collect(Collectors.joining(",")))
        );
    }

    public static String formatIngredients(Map<Ingredient<ItemStack>, Integer> ingredients) {
        return ingredients
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().displayName() + "/" + entry.getValue())
                .collect(Collectors.joining(","));
    }
}
