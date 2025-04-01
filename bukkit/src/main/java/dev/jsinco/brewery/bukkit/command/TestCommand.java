package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.effect.text.DrunkTextTransformer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        switch (args[0]) {
            case "info" -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                player.sendMessage(BrewAdapter.fromItem(item).toString());
            }
            case "drunktext" -> {
                int alcohol = Integer.parseInt(args[1]);
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                builder.replace(builder.length() - 1, builder.length(), "");
                sender.sendMessage("Drunken text at alcohol " + alcohol + ": " + DrunkTextTransformer.transform(builder.toString(), TheBrewingProject.getInstance().getDrunkTextRegistry(), alcohol));
            }
            default -> {
            }
        }
        return true;
    }
}
