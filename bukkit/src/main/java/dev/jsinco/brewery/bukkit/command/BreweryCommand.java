package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.effect.DrunkEventAction;
import dev.jsinco.brewery.effect.DrunkEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BreweryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can only run brewery commands as a player");
            return true;
        }
        return switch (args[0]) {
            case "create" -> CreateCommand.onCommand(player, Arrays.copyOfRange(args, 1, args.length));
            case "puke" -> {
                DrunkEventAction.doDrunkEvent(player.getUniqueId(), DrunkEvent.PUKE);
                yield true;
            }
            default -> false;
        };
    }
}
