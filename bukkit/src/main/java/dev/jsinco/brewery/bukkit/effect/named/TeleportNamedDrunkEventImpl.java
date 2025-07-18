package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.bukkit.util.BukkitMessageUtil;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.named.TeleportNamedDrunkEvent;
import dev.jsinco.brewery.util.Holder;
import dev.jsinco.brewery.vector.BreweryLocation;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class TeleportNamedDrunkEventImpl extends TeleportNamedDrunkEvent {


    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer.value());
        if (player == null) {
            return;
        }

        List<BreweryLocation> locations = Config.config().events().teleportDestinations().stream().map(Supplier::get).toList();
        if (locations.isEmpty()) {
            return;
        }
        BreweryLocation teleport = locations.get(RANDOM.nextInt(locations.size()));
        Location location = BukkitAdapter.toLocation(teleport);
        player.teleport(location);
        player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.TELEPORT_MESSAGE, BukkitMessageUtil.getPlayerTagResolver(player)));
    }
}
