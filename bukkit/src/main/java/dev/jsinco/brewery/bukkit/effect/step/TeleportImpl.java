package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.step.Teleport;
import dev.jsinco.brewery.util.Holder;
import dev.jsinco.brewery.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class TeleportImpl extends Teleport {

    public TeleportImpl(Supplier<BreweryLocation> location) {
        super(location);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer.value());
        if (player == null) {
            return;
        }

        player.teleport(BukkitAdapter.toLocation(getLocation().get()));
    }
}
