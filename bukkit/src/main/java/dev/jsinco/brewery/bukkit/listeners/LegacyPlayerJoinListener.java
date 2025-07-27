package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.effect.DrunksManager;
import dev.jsinco.brewery.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Random;
import java.util.UUID;

public class LegacyPlayerJoinListener implements Listener {
    private final static Random RANDOM = new Random();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        DrunksManager drunksManager = TheBrewingProject.getInstance().getDrunksManager();
        DrunkState drunkState = drunksManager.getDrunkState(playerUuid);
        String playerName = event.getPlayer().getName();
        if (drunksManager.isPassedOut(playerUuid)) {
            String kickEventMessage = Config.config().events().kickEvent().kickEventMessage();
            event.kickMessage(
                    MiniMessage.miniMessage().deserialize(kickEventMessage == null ? TranslationsConfig.KICK_EVENT_MESSAGE : kickEventMessage,
                            MessageUtil.getDrunkStateTagResolver(drunkState), Placeholder.unparsed("player_name", playerName == null ? "" : playerName)
                    )
            );
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (Config.config().events().drunkenJoinDeny() && drunkState != null && drunkState.alcohol() >= 85 && RANDOM.nextInt(15) <= drunkState.alcohol() - 85) {
            event.kickMessage(
                    MiniMessage.miniMessage().deserialize(TranslationsConfig.DRUNKEN_JOIN_DENY_MESSAGE,
                            MessageUtil.getDrunkStateTagResolver(drunkState), Placeholder.unparsed("player_name", playerName == null ? "" : playerName)
                    )
            );
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
}
