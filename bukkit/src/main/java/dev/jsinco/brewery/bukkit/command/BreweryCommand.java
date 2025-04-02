package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.effect.event.DrunkEventExecutor;
import dev.jsinco.brewery.bukkit.effect.event.NamedDrunkEventExecutor;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
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
            case "event" -> {
                NamedDrunkEvent namedDrunkEvent = Registry.DRUNK_EVENT.get(BreweryKey.parse(args[1]));
                if (namedDrunkEvent != null) {
                    NamedDrunkEventExecutor.doDrunkEvent(player.getUniqueId(), namedDrunkEvent);
                    yield true;
                }
                DrunkEventExecutor.doDrunkEvent(player.getUniqueId(), TheBrewingProject.getInstance().getCustomDrunkEventRegistry().getCustomEvent(BreweryKey.parse(args[1])));
                yield true;
            }
            case "status" -> StatusCommand.onCommand(player, Arrays.copyOfRange(args, 1, args.length));
            case "info" -> InfoCommand.onCommand(player);
            default -> false;
        };
    }
}
