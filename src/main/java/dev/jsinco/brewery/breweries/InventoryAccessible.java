package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.util.UUID;

public interface InventoryAccessible<I> {

    void open(BreweryLocation breweryLocation, UUID playerUuid);

    boolean inventoryAllows(UUID playerUuid, I item);
}
