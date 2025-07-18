package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

/**
 * Represents the "CHICKEN" named drunk event.
 * This class is a layer between the NamedDrunkEvent and the implementation,
 * this class is meant to only be used for "data" purposes and cannot be executed directly.
 * This class is non-sealed so child modules such as "bukkit" may extend it and implement the execute method.
 * <p>
 * <p>
 * Child modules should always upgrade to the implementation of this class sometime before execution,
 * see {@link dev.jsinco.brewery.event.EventStepRegistry}
 */
public non-sealed class ChickenNamedDrunkEvent extends NamedDrunkEvent {
    public ChickenNamedDrunkEvent() {
        super(99, 50, 1, "CHICKEN");
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        throw new IllegalEventStepCall();
    }

    @Override
    public void register(EventStepRegistry registry) {
        throw new IllegalEventStepCall();
    }
}
