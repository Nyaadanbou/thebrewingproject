package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.effect.event.DrunkEventExecutor;
import dev.jsinco.brewery.bukkit.effect.event.NamedDrunkEventExecutor;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.event.DrunkEvent;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class BreweryCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can only run brewery commands as a player");
            return true;
        }
        try {
            SubCommand subCommand = SubCommand.valueOf(args[0].toUpperCase(Locale.ROOT));
            if (!sender.hasPermission(subCommand.getPermissionNode())) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                return true;
            }
            return switch (subCommand) {
                case CREATE -> CreateCommand.onCommand(player, Arrays.copyOfRange(args, 1, args.length));
                case EVENT -> {
                    NamedDrunkEvent namedDrunkEvent = Registry.DRUNK_EVENT.get(BreweryKey.parse(args[1]));
                    if (namedDrunkEvent != null) {
                        NamedDrunkEventExecutor.doDrunkEvent(player.getUniqueId(), namedDrunkEvent);
                        yield true;
                    }
                    DrunkEventExecutor.doDrunkEvent(player.getUniqueId(), TheBrewingProject.getInstance().getCustomDrunkEventRegistry().getCustomEvent(BreweryKey.parse(args[1])));
                    yield true;
                }
                case STATUS -> StatusCommand.onCommand(player, Arrays.copyOfRange(args, 1, args.length));
                case INFO -> InfoCommand.onCommand(player);
                case RELOAD -> {
                    if (!player.hasPermission("brewery.command.reload")) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                        yield true;
                    }
                    TheBrewingProject.getInstance().reload();
                    player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_RELOAD_MESSAGE));
                    yield true;
                }
                case SEAL -> {
                    if (!player.hasPermission("brewery.command.seal")) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
                        yield true;
                    }
                    BrewAdapter.seal(player.getInventory().getItemInMainHand(), args.length > 1 ? LegacyComponentSerializer.legacyAmpersand().deserialize(
                            String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                    ) : null);
                    yield true;
                }
            };
        } catch (IndexOutOfBoundsException e) {
            // Lazy handling
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_MISSING_ARGUMENT));
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_ILLEGAL_ARGUMENT));
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            return List.of();
        }
        if (args.length == 1) {
            return Arrays.stream(SubCommand.values())
                    .filter(subCommand -> sender.hasPermission(subCommand.getPermissionNode()))
                    .map(SubCommand::name)
                    .map(subCommand -> subCommand.toLowerCase(Locale.ROOT))
                    .filter(subCommand -> subCommand.startsWith(args[0]))
                    .toList();
        }
        SubCommand subCommand;
        try {
            subCommand = SubCommand.valueOf(args[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
        return switch (subCommand) {
            case CREATE -> CreateCommand.tabComplete(Arrays.copyOfRange(args, 1, args.length));
            case EVENT -> {
                if (args.length > 2) {
                    yield List.of();
                }
                yield Stream.concat(Arrays.stream(NamedDrunkEvent.values()), TheBrewingProject.getInstance().getCustomDrunkEventRegistry().events().stream())
                        .map(DrunkEvent::key)
                        .map(BreweryKey::key)
                        .filter(drunkEvent -> drunkEvent.startsWith(args[1]))
                        .toList();
            }
            case INFO -> List.of();
            case SEAL -> {
                if (args.length == 2) {
                    yield List.of("<volume>");
                }
                yield List.of();
            }
            case STATUS -> StatusCommand.tabComplete(Arrays.copyOfRange(args, 1, args.length));
            case RELOAD -> List.of();
        };
    }
}
