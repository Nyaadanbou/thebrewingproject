package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

/**
 * Interrupts the player's movement and causes them to stumble.
 * <p>
 * Child modules should always upgrade to the implementation of this class sometime before execution,
 * see {@link dev.jsinco.brewery.event.EventStepRegistry}
 */
public non-sealed class StumbleNamedDrunkEvent extends NamedDrunkEvent {
    public StumbleNamedDrunkEvent() {
        super(25, 0, 100, "STUMBLE");
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
