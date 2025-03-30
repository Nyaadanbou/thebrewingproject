package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.RandomUtil;
import dev.jsinco.brewery.util.Registry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class DrunkManager {

    private final int inverseDecayRate;
    private Map<UUID, DrunkState> drunks = new HashMap<>();
    @Getter
    private long drunkManagerTime = 0;
    private Map<Long, Map<UUID, DrunkEvent>> events = new HashMap<>();
    private Map<UUID, Long> plannedEvents = new HashMap<>();
    private int lowestLevelRequiredForEvent = Integer.MAX_VALUE;

    private static final Random RANDOM = new Random();
    private final Map<UUID, Long> passedOut = new HashMap<>();

    public DrunkManager(int inverseDecayRate) {
        this.inverseDecayRate = inverseDecayRate;
        for (DrunkEvent drunkEvent : Registry.DRUNK_EVENT.values()) {
            if (drunkEvent.getAlcohol() < lowestLevelRequiredForEvent) {
                lowestLevelRequiredForEvent = drunkEvent.getAlcohol();
            }
        }
    }

    public void consume(UUID playerUuid, int alcohol, int toxins) {
        this.consume(playerUuid, alcohol, toxins, drunkManagerTime);
    }

    /**
     * @param playerUuid
     * @param alcohol
     * @param timestamp  Should be in relation to the internal clock in drunk manager
     */
    public void consume(UUID playerUuid, int alcohol, int toxins, long timestamp) {
        boolean alreadyDrunk = drunks.containsKey(playerUuid);
        DrunkState drunkState = alreadyDrunk ?
                drunks.get(playerUuid).recalculate(inverseDecayRate, timestamp).addAlcohol(alcohol, toxins) : new DrunkState(alcohol, toxins, timestamp);
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
        boolean alreadyDrunk = drunks.containsKey(playerUuid);
        return alreadyDrunk ?
                drunks.get(playerUuid).recalculate(inverseDecayRate, drunkManagerTime) : null;

    }

    public void clear(UUID playerUuid) {
        Long plannedEventTime = plannedEvents.remove(playerUuid);
        drunks.remove(playerUuid);
        if (plannedEventTime == null) {
            return;
        }
        if (events.containsKey(plannedEventTime)) {
            events.get(plannedEventTime).remove(playerUuid);
        }
    }

    public void tick(BiConsumer<UUID, DrunkEvent> action) {
        Map<UUID, DrunkEvent> currentEvents = events.remove(drunkManagerTime++);
        List<UUID> wokenUp = passedOut.entrySet()
                .stream()
                .filter(entry -> entry.getValue() + Config.KICK_EVENT_DURATION < drunkManagerTime)
                .map(Map.Entry::getKey)
                .toList();
        wokenUp.forEach(passedOut::remove);
        if (currentEvents == null) {
            return;
        }
        Set<UUID> toRemove = new HashSet<>();
        for (UUID currentEvent : currentEvents.keySet()) {
            if (!drunks.containsKey(currentEvent)) {
                toRemove.add(currentEvent);
            }
        }
        currentEvents.forEach((key, value) -> plannedEvents.remove(key));
        toRemove.forEach(currentEvents::remove);
        currentEvents.forEach(action);
        currentEvents.forEach((playerUuid, event) -> planEvent(playerUuid));
    }

    public void planEvent(UUID playerUuid) {
        if (plannedEvents.containsKey(playerUuid)) {
            return;
        }
        DrunkState drunkState = getDrunkState(playerUuid);
        if (drunkState == null) {
            return;
        }
        if (drunkState.alcohol() < lowestLevelRequiredForEvent) {
            return;
        }
        List<DrunkEvent> drunkEvents = Registry.DRUNK_EVENT.values()
                .stream()
                .filter(drunkEvent -> drunkEvent.getAlcohol() <= drunkState.alcohol())
                .toList();
        DrunkEvent drunkEvent = RandomUtil.randomWeighted(drunkEvents);
        long time = (long) (drunkManagerTime + Math.max(1, RANDOM.nextGaussian(200, 100)));
        events.computeIfAbsent(time, ignored -> new HashMap<>()).put(playerUuid, drunkEvent);
        plannedEvents.put(playerUuid, time);
    }

    public void registerPassedOut(@NotNull UUID uniqueId) {
        this.passedOut.put(uniqueId, drunkManagerTime);
    }

    public boolean isPassedOut(@NotNull UUID uniqueId) {
        return passedOut.containsKey(uniqueId);
    }

    public @Nullable Pair<DrunkEvent, Long> getPlannedEvent(UUID uniqueId) {
        Long time = plannedEvents.get(uniqueId);
        if (time == null) {
            return null;
        }
        return new Pair<>(events.get(time).get(uniqueId), time);
    }
}
