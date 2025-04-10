package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.recipe.RecipeEffects;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.BrewScore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InfoCommand {
    public static boolean onCommand(Player player) {
        if (!player.hasPermission("brewery.command.info")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        Optional<Brew> brewOptional = BrewAdapter.fromItem(item);
        brewOptional
                .ifPresent(brew -> player.sendMessage(
                        MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_BREW_MESSAGE,
                                MessageUtil.getScoreTagResolver(brew.closestRecipe(TheBrewingProject.getInstance().getRecipeRegistry())
                                        .map(brew::score)
                                        .orElse(BrewScore.NONE)),
                                Placeholder.component("brewing_step_info", MessageUtil.compileBrewInfo(brew, true)
                                        .collect(Component.toComponent(Component.text("\n")))
                                )
                        )
                ));
        Optional<RecipeEffects> recipeEffectsOptional = RecipeEffects.fromItem(item);
        recipeEffectsOptional.ifPresent(effects -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_EFFECT_MESSAGE, MessageUtil.recipeEffectResolver(effects)));
        });
        if (brewOptional.isEmpty() && recipeEffectsOptional.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_NOT_A_BREW));
        }
        return true;
    }
}
