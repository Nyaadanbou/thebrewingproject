package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.effect.DrunkManager;
import dev.jsinco.brewery.effect.DrunkState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static Component compilePlayerMessage(String message, Player player, DrunkManager drunkManager, int alcohol) {
        DrunkState drunkState = drunkManager.getDrunkState(player.getUniqueId());
        return MiniMessage.miniMessage().deserialize(
                message,
                Placeholder.component("player_name", player.name()),
                Placeholder.component("team_name", player.teamDisplayName()),
                Placeholder.parsed("alcohol", String.valueOf(alcohol)),
                Placeholder.parsed("player_alcohol", String.valueOf(drunkState == null ? "0" : drunkState.alcohol())),
                Placeholder.unparsed("world", player.getWorld().getName())
        );
    }
}
