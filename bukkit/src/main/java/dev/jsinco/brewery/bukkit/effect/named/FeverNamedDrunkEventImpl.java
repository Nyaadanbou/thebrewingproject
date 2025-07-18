package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.named.FeverNamedDrunkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class FeverNamedDrunkEventImpl extends FeverNamedDrunkEvent {

    private static final int AFFECT_DURATION = 200; // 10 seconds

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        new BukkitRunnable() {
            int ticksRan = 0;
            @Override
            public void run() {
                if (ticksRan++ >= AFFECT_DURATION) {
                    cancel();
                    return;
                }
                player.setFreezeTicks(player.getMaxFreezeTicks());
            }
        }.runTaskTimer(TheBrewingProject.getInstance(), 0, 1);
        player.setFireTicks(AFFECT_DURATION / 2);
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(FeverNamedDrunkEvent.class, original -> new FeverNamedDrunkEventImpl());
    }
}
