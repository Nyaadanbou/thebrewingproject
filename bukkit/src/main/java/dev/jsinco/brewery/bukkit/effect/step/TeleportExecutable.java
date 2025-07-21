package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TeleportExecutable implements ExecutableEventStep {

    private final Supplier<BreweryLocation> location;

    public TeleportExecutable(Supplier<BreweryLocation> location) {
        this.location = location;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        player.teleportAsync(BukkitAdapter.toLocation(location.get()));
    }

}
