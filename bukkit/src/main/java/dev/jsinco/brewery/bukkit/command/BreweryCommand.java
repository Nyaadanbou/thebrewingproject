package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.effect.event.DrunkEventExecutor;
import dev.jsinco.brewery.bukkit.effect.event.NamedDrunkEventExecutor;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                if (!player.hasPermission("brewery.command.event")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                    yield true;
                }
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
            case "reload" -> {
                if (!player.hasPermission("brewery.command.reload")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                    yield true;
                }
                TheBrewingProject.getInstance().reload();
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_RELOAD_MESSAGE));
                yield true;
            }
            case "seal" -> {
                if (!player.hasPermission("brewery.command.seal")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                    yield true;
                }
                BrewAdapter.seal(player.getInventory().getItemInMainHand(), args.length > 1 ? LegacyComponentSerializer.legacyAmpersand().deserialize(
                        String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                ) : null);
                yield true;
            }
            default -> false;
        };
    }
}
