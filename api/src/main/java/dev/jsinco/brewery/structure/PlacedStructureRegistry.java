package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.breweries.StructureHolder;
import dev.jsinco.brewery.vector.BreweryLocation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlacedStructureRegistry {

    void registerStructure(MultiBlockStructure<?> placedBreweryStructure);

    void unregisterStructure(MultiBlockStructure<?> structure);

    Optional<MultiBlockStructure<?>> getStructure(BreweryLocation location);

    Set<MultiBlockStructure<?>> getStructures(Collection<BreweryLocation> locations);

    Optional<StructureHolder<?>> getHolder(BreweryLocation location);

    void unloadWorld(UUID worldUuid);

    void clear();
}
