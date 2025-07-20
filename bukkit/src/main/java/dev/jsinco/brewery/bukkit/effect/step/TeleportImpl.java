package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.Teleport;
import dev.jsinco.brewery.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TeleportImpl extends Teleport implements ExecutableEventStep {

    public TeleportImpl(Supplier<BreweryLocation> location) {
        super(location);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        player.teleportAsync(BukkitAdapter.toLocation(getLocation().get()));
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(Teleport.class, original -> {
            Teleport event = (Teleport) original;
            return new TeleportImpl(event.getLocation());
        });
    }
}
