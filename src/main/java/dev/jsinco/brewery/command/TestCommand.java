package dev.jsinco.brewery.command;

import dev.jsinco.brewery.brews.Brew;
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
                player.sendMessage(Brew.fromItem(item).toString());
            }
            default -> {}
        }
        return true;
    }
}
