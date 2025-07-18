package dev.jsinco.brewery.bukkit.effect.event;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.event.CustomEvent;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.util.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DrunkEventExecutor {

    // I know this a bit hacky, but the only other alternative I can think of is manually
    //  listing all these classes out.
    private static final String[] PACKS = {
            "dev.jsinco.brewery.bukkit.effect.named",
            "dev.jsinco.brewery.bukkit.effect.step"
    };

    private final Map<UUID, List<EventStep>> onJoinExecutions = new HashMap<>();

    public DrunkEventExecutor() {
        EventStepRegistry registry = TheBrewingProject.getInstance().getEventStepRegistry();
        for (String pack : PACKS) {
            Registry.assignableClasses(EventStep.class, pack).forEach(eventStep -> {
                eventStep.register(registry);
            });
        }
    }

    public void doDrunkEvent(UUID playerUuid, EventStep event) {
        doDrunkEvents(playerUuid, List.of(event));
    }

    public void doDrunkEvents(UUID playerUuid, List<EventStep> events) {
        EventStepRegistry registry = TheBrewingProject.getInstance().getEventStepRegistry();

        for (int i = 0; i < events.size(); i++) {
            final EventStep event = events.get(i);

            if (event instanceof CustomEvent customEvent) {
                // Custom events are special <- TODO: This could use EventStepRegistry upgrading.
                TheBrewingProject.getInstance().getDrunkEventExecutor().doDrunkEvents(playerUuid, customEvent.getSteps());
            } else {
                EventStep upgradedEvent = registry.upgrade(event);
                upgradedEvent.execute(playerUuid, events, i);
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
