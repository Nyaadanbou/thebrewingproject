package dev.jsinco.brewery.effect.event;

import dev.jsinco.brewery.util.vector.BreweryLocation;

public record Teleport(BreweryLocation location, String teleportType) implements EventStep {
}
