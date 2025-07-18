package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

/**
 * Sends a fake block update to the block the player is looking at, the block will change to
 * a random material.
 * Changed blocks have no effect on the world and are not real.
 * <p>
 * Child modules should always upgrade to the implementation of this class sometime before execution,
 * see {@link dev.jsinco.brewery.event.EventStepRegistry}
 */
public non-sealed class HallucinationNamedDrunkEvent extends NamedDrunkEvent {

    public HallucinationNamedDrunkEvent() {
        super(70, 25, 35, "HALLUCINATION");
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
