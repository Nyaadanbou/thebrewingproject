package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.vector.BreweryLocation;

import java.util.function.Supplier;

public class Teleport implements EventStep {

    private final Supplier<BreweryLocation> location;

    public Teleport(Supplier<BreweryLocation> location) {
        this.location = location;
    }

    public Supplier<BreweryLocation> getLocation() {
        return location;
    }

}
