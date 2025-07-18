package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

/**
 * Causes the player to spew random items from their eye location.
 * <p>
 * Child modules should always upgrade to the implementation of this class sometime before execution,
 * see {@link dev.jsinco.brewery.event.EventStepRegistry}
 */
public non-sealed class PukeNamedDrunkEvent extends NamedDrunkEvent {

    public PukeNamedDrunkEvent() {
        super(45, 45, 20, "PUKE");
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
