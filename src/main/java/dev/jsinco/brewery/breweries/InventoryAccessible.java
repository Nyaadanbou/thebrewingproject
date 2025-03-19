package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.util.UUID;

public interface InventoryAccessible {

    void open(BreweryLocation breweryLocation, UUID playerUuid);
}
