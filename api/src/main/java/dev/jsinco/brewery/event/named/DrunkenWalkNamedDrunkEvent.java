package dev.jsinco.brewery.event.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;

import java.util.List;
import java.util.UUID;

public non-sealed class DrunkenWalkNamedDrunkEvent extends NamedDrunkEvent {
    public DrunkenWalkNamedDrunkEvent() {
        super(60, 20, 20, "DRUNKEN_WALK");
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
