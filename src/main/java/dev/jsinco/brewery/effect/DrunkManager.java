package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.util.RandomUtil;
import dev.jsinco.brewery.util.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class DrunkManager {

    private final int inverseDecayRate;
    private Map<UUID, DrunkState> drunks = new HashMap<>();
    private long drunkManagerTime = 0;
    private Map<Long, Map<UUID, DrunkEvent>> events = new HashMap<>();
    private Map<UUID, Long> plannedEvents = new HashMap<>();
    private int lowestLevelRequiredForEvent = Integer.MAX_VALUE;

    private static final Random RANDOM = new Random();

    public DrunkManager(int inverseDecayRate) {
        this.inverseDecayRate = inverseDecayRate;
        for (DrunkEvent drunkEvent : Registry.DRUNK_EVENT.values()) {
            if (drunkEvent.getAlcohol() < lowestLevelRequiredForEvent) {
                lowestLevelRequiredForEvent = drunkEvent.getAlcohol();
            }
        }
    }

    public void consume(UUID playerUuid, int alcohol, long timeStamp) {
        boolean alreadyDrunk = drunks.containsKey(playerUuid);
        DrunkState drunkState = alreadyDrunk ?
                drunks.get(playerUuid).recalculate(inverseDecayRate, timeStamp).addAlcohol(alcohol) : new DrunkState(alcohol, timeStamp);
        if (drunkState.alcohol() <= 0) {
            drunks.remove(playerUuid);
            return;
        }
        drunks.put(playerUuid, drunkState);
        if (drunkState.alcohol() >= lowestLevelRequiredForEvent) {
            planEvent(playerUuid);
        }
    }

    public @Nullable DrunkState getDrunkState(UUID playerUuid) {
        return drunks.get(playerUuid);
    }

    public void clear(UUID playerUuid) {
        Long plannedEventTime = plannedEvents.get(playerUuid);
        drunks.remove(playerUuid);
        if (plannedEventTime == null) {
            return;
        }
        events.get(plannedEventTime).remove(playerUuid);
    }

    public void tick(BiConsumer<UUID, DrunkEvent> action) {
        Map<UUID, DrunkEvent> currentEvents = events.remove(drunkManagerTime++);
        if (currentEvents == null) {
            return;
        }
        Set<UUID> toRemove = new HashSet<>();
        for (UUID currentEvent : currentEvents.keySet()) {
            if (!drunks.containsKey(currentEvent)) {
                toRemove.add(currentEvent);
            }
        }
        toRemove.forEach(currentEvents::remove);
        toRemove.forEach(plannedEvents::remove);
        currentEvents.forEach(action);
        currentEvents.forEach((playerUuid, event) -> planEvent(playerUuid));
    }

    public void planEvent(UUID playerUuid) {
        if (plannedEvents.containsKey(playerUuid)) {
            return;
        }
        DrunkState drunkState = drunks.get(playerUuid);
        if (drunkState == null) {
            return;
        }
        List<DrunkEvent> drunkEvents = List.copyOf(Registry.DRUNK_EVENT.values());
        DrunkEvent drunkEvent = RandomUtil.randomWeighted(drunkEvents);
        long time = (long) (drunkManagerTime + Math.max(1, RANDOM.nextGaussian(1000, 500)));
        events.computeIfAbsent(time, ignored -> new HashMap<>()).put(playerUuid, drunkEvent);
        plannedEvents.put(playerUuid, time);
    }
}
