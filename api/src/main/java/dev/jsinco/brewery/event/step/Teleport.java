package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;
import dev.jsinco.brewery.vector.BreweryLocation;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class Teleport implements EventStep {

    private final Supplier<BreweryLocation> location;

    public Teleport(Supplier<BreweryLocation> location) {
        this.location = location;
    }

    public Supplier<BreweryLocation> getLocation() {
        return location;
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
