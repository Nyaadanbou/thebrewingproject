package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.recipe.RecipeEffects;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.BrewScore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfoCommand {
    public static boolean onCommand(Player player) {
        if (!player.hasPermission("brewery.command.info")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        BrewAdapter.fromItem(item)
                .ifPresentOrElse(brew -> player.sendMessage(
                        MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_BREW_MESSAGE,
                                MessageUtil.getBrewTagResolver(brew),
                                MessageUtil.getScoreTagResolver(brew.closestRecipe(TheBrewingProject.getInstance().getRecipeRegistry())
                                        .map(brew::score)
                                        .orElse(BrewScore.NONE))
                        )), () -> player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_NOT_A_BREW)));
        RecipeEffects.fromItem(item)
                .ifPresent(effects -> {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_INFO_EFFECT_MESSAGE, MessageUtil.recipeEffectResolver(effects)));
                });
        return true;
    }
}
