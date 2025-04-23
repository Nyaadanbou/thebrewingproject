package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkStateImpl;
import dev.jsinco.brewery.effect.DrunksManagerImpl;
import dev.jsinco.brewery.event.DrunkEvent;
import dev.jsinco.brewery.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StatusCommand {
    public static boolean onCommand(OfflinePlayer player, CommandSender sender, @NotNull String[] args) {
        DrunksManagerImpl<?> drunksManager = TheBrewingProject.getInstance().getDrunksManager();
        return switch (args[0]) {
            case "info" -> StatusCommand.info(player, sender, drunksManager, Arrays.copyOfRange(args, 1, args.length));
            case "consume" ->
                    StatusCommand.consume(player, sender, drunksManager, Arrays.copyOfRange(args, 1, args.length));
            case "clear" ->
                    StatusCommand.clear(player, sender, drunksManager, Arrays.copyOfRange(args, 1, args.length));
            case "set" -> StatusCommand.set(player, sender, drunksManager, Arrays.copyOfRange(args, 1, args.length));
            default -> false;
        };
    }

    private static boolean set(OfflinePlayer target, CommandSender sender, DrunksManagerImpl<?> drunksManager, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_MISSING_ARGUMENT, Placeholder.unparsed("argument_type", "<alcohol>")));
            return true;
        }
        drunksManager.clear(target.getUniqueId());
        return consume(target, sender, drunksManager, args);
    }

    private static boolean clear(@NotNull OfflinePlayer target, CommandSender sender, DrunksManagerImpl<?> drunksManager, @NotNull String[] args) {
        drunksManager.clear(target.getUniqueId());
        sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_STATUS_CLEAR_MESSAGE, Placeholder.unparsed("player_name", target.getName())));
        return true;
    }

    private static boolean consume(OfflinePlayer target, CommandSender sender, DrunksManagerImpl<?> drunksManager, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_MISSING_ARGUMENT, Placeholder.unparsed("argument_type", "<alcohol>")));
            return true;
        }
        drunksManager.clear(target.getUniqueId());
        int alcohol = Integer.parseInt(args[0]);
        int toxins;
        if (args.length == 2) {
            toxins = Integer.parseInt(args[1]);
        } else {
            toxins = 0;
        }
        drunksManager.consume(target.getUniqueId(), alcohol, toxins);
        sender.sendMessage(compileStatusMessage(target, drunksManager, TranslationsConfig.COMMAND_STATUS_SET_MESSAGE));
        return true;
    }

    private static boolean info(OfflinePlayer target, CommandSender sender, DrunksManagerImpl<?> drunksManager, @NotNull String[] args) {
        if (target == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_UNKNOWN_PLAYER, Placeholder.unparsed("player_name", args[0])));
            return true;
        }
        sender.sendMessage(compileStatusMessage(target, drunksManager, TranslationsConfig.COMMAND_STATUS_INFO_MESSAGE));
        return true;
    }

    private static Component compileStatusMessage(OfflinePlayer target, DrunksManagerImpl<?> drunksManager, String message) {
        DrunkStateImpl drunkState = drunksManager.getDrunkState(target.getUniqueId());
        Pair<DrunkEvent, Long> nextEvent = drunksManager.getPlannedEvent(target.getUniqueId());
        drunksManager.getPlannedEvent(target.getUniqueId());
        String targetName = target.getName();
        return MiniMessage.miniMessage().deserialize(
                message,
                Formatter.number("alcohol", drunkState == null ? 0 : drunkState.alcohol()),
                Formatter.number("toxins", drunkState == null ? 0 : drunkState.toxins()),
                Placeholder.unparsed("player_name", targetName == null ? "null" : targetName),
                Formatter.number("next_event_time", nextEvent == null ? 0 : nextEvent.second() - TheBrewingProject.getInstance().getTime()),
                Placeholder.unparsed("next_event", nextEvent == null ? TranslationsConfig.NO_EVENT_PLANNED : nextEvent.first().displayName())
        );
    }

    public static List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("info", "consume", "set", "clear")
                    .toList();
        }
        return switch (args[0]) {
            case "consume", "set" -> {
                if (args.length == 2) {
                    yield BreweryCommand.INTEGER_TAB_COMPLETIONS;
                } else if (args.length == 3) {
                    yield BreweryCommand.INTEGER_TAB_COMPLETIONS;
                }
                yield List.of();
            }
            default -> List.of();
        };
    }
}
