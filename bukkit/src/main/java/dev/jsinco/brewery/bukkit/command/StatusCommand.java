package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkManager;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.effect.event.DrunkEvent;
import dev.jsinco.brewery.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class StatusCommand {
    public static boolean onCommand(Player player, @NotNull String[] args) {
        if (!player.hasPermission("brewery.command.status")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
            return true;
        }
        DrunkManager drunkManager = TheBrewingProject.getInstance().getDrunkManager();
        return switch (args[0]) {
            case "info" -> StatusCommand.info(player, drunkManager, Arrays.copyOfRange(args, 1, args.length));
            case "consume" -> StatusCommand.consume(player, drunkManager, Arrays.copyOfRange(args, 1, args.length));
            case "clear" -> StatusCommand.clear(player, drunkManager, Arrays.copyOfRange(args, 1, args.length));
            case "set" -> StatusCommand.set(player, drunkManager, Arrays.copyOfRange(args, 1, args.length));
            default -> false;
        };
    }

    private static @Nullable OfflinePlayer getTarget(Player requester, String[] args) {
        if (args.length == 0) {
            return requester;
        } else {
            return Bukkit.getOfflinePlayerIfCached(args[0]);
        }
    }

    private static boolean set(Player requester, DrunkManager drunkManager, @NotNull String[] args) {
        if (args.length < 2) {
            return false;
        }
        OfflinePlayer target;
        String[] subArgs;
        try {
            Integer.parseInt(args[0]);
            target = requester;
            subArgs = args;
        } catch (NumberFormatException e) {
            target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (target == null) {
                requester.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_UNKNOWN_PLAYER, Placeholder.unparsed("player_name", args[0])));
                return true;
            }
            subArgs = Arrays.copyOfRange(args, 1, args.length);
        }
        drunkManager.clear(target.getUniqueId());
        int alcohol = Integer.parseInt(subArgs[0]);
        int toxins;
        if (subArgs.length == 2) {
            toxins = Integer.parseInt(subArgs[1]);
        } else {
            toxins = 0;
        }
        drunkManager.consume(target.getUniqueId(), alcohol, toxins);
        requester.sendMessage(compileStatusMessage(target, drunkManager, TranslationsConfig.COMMAND_STATUS_SET_MESSAGE));
        return true;
    }

    private static boolean clear(@NotNull Player requester, DrunkManager drunkManager, @NotNull String[] args) {
        OfflinePlayer target = getTarget(requester, args);
        if (target == null) {
            requester.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_UNKNOWN_PLAYER, Placeholder.unparsed("player_name", args[0])));
            return true;
        }
        drunkManager.clear(target.getUniqueId());
        requester.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_STATUS_CLEAR_MESSAGE, Placeholder.unparsed("player_name", target.getName())));
        return true;
    }

    private static boolean consume(Player requester, DrunkManager drunkManager, @NotNull String[] args) {
        if (args.length < 2) {
            return false;
        }
        OfflinePlayer target;
        String[] subArgs;
        try {
            Integer.parseInt(args[0]);
            target = requester;
            subArgs = args;
        } catch (NumberFormatException e) {
            target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (target == null) {
                requester.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_UNKNOWN_PLAYER, Placeholder.unparsed("player_name", args[0])));
                return true;
            }
            subArgs = Arrays.copyOfRange(args, 1, args.length);
        }
        int alcohol = Integer.parseInt(subArgs[0]);
        int toxins;
        if (subArgs.length == 2) {
            toxins = Integer.parseInt(subArgs[1]);
        } else {
            toxins = 0;
        }
        drunkManager.consume(target.getUniqueId(), alcohol, toxins);
        requester.sendMessage(compileStatusMessage(target, drunkManager, TranslationsConfig.COMMAND_STATUS_CONSUME_MESSAGE));
        return true;
    }

    private static boolean info(Player requester, DrunkManager drunkManager, @NotNull String[] args) {
        OfflinePlayer target = getTarget(requester, args);
        if (target == null) {
            requester.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_UNKNOWN_PLAYER, Placeholder.unparsed("player_name", args[0])));
            return true;
        }
        requester.sendMessage(compileStatusMessage(target, drunkManager, TranslationsConfig.COMMAND_STATUS_INFO_MESSAGE));
        return true;
    }

    private static Component compileStatusMessage(OfflinePlayer target, DrunkManager drunkManager, String message) {
        DrunkState drunkState = drunkManager.getDrunkState(target.getUniqueId());
        Pair<DrunkEvent, Long> nextEvent = drunkManager.getPlannedEvent(target.getUniqueId());
        drunkManager.getPlannedEvent(target.getUniqueId());
        return MiniMessage.miniMessage().deserialize(
                message,
                Formatter.number("alcohol", drunkState == null ? 0 : drunkState.alcohol()),
                Formatter.number("toxins", drunkState == null ? 0 : drunkState.toxins()),
                Placeholder.unparsed("player_name", target.getName()),
                Formatter.number("next_event_time", nextEvent == null ? 0 : nextEvent.second() - drunkManager.getDrunkManagerTime()),
                Placeholder.unparsed("next_event", nextEvent == null ? TranslationsConfig.NO_EVENT_PLANNED : nextEvent.first().getTranslation())
        );
    }
}
