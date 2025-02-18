package dev.jsinco.brewery.util.vector;

import java.util.UUID;

public record BreweryLocation(int x, int y, int z, UUID worldUuid) {

    public BreweryVector toVector() {
        return new BreweryVector(x, y, z);
    }
}
