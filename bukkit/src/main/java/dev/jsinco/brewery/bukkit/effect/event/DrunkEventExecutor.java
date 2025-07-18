package dev.jsinco.brewery.bukkit.effect.event;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.CustomEvent;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.util.Holder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DrunkEventExecutor {

    private Map<UUID, List<EventStep>> onJoinExecutions = new HashMap<>();

    public void doDrunkEvent(UUID playerUuid, EventStep event) {
        doDrunkEvents(playerUuid, List.of(event));
    }

    public void doDrunkEvents(UUID playerUuid, List<EventStep> events) {

        for (int i = 0; i < events.size(); i++) {
            final EventStep event = events.get(i);

            if (event instanceof CustomEvent customEvent) {
                // Custom events are special
                TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(playerUuid, customEvent.getSteps());
            } else {
                try {
                    event.execute(new Holder.Player(playerUuid), events, i);
                    System.out.println("step sucessful!" + event.getClass().getSimpleName() + " for player " + playerUuid);
                } catch (Exception e) {
                    TheBrewingProject.getInstance().getLogger().severe("Error executing drunk event for player " + playerUuid + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void onPlayerJoin(UUID playerUuid) {
        List<EventStep> eventSteps = onJoinExecutions.get(playerUuid);
        if (eventSteps == null) {
            return;
        }
        doDrunkEvents(playerUuid, eventSteps);
    }

    public void add(UUID playerUuid, List<EventStep> events) {
        onJoinExecutions.put(playerUuid, events);
    }

    public void clear(UUID playerUuid) {
        onJoinExecutions.remove(playerUuid);
    }

    public void clear() {
        onJoinExecutions.clear();
    }
}
