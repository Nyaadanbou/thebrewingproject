package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.breweries.StructureHolder;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import dev.jsinco.brewery.util.vector.BreweryVector;

import java.util.*;

public class PlacedStructureRegistry {

    private final Map<UUID, Map<BreweryVector, MultiBlockStructure<? extends StructureHolder<?>>>> structures = new HashMap<>();

    public void registerStructure(MultiBlockStructure<?> placedBreweryStructure) {
        for (BreweryLocation location : placedBreweryStructure.positions()) {
            UUID worldUuid = location.worldUuid();
            structures.computeIfAbsent(worldUuid, ignored -> new HashMap<>()).put(location.toVector(), placedBreweryStructure);
        }
    }

    public void unregisterStructure(MultiBlockStructure<?> structure) {
        for (BreweryLocation location : structure.positions()) {
            UUID worldUuid = location.worldUuid();
            structures.computeIfAbsent(worldUuid, ignored -> new HashMap<>()).remove(location.toVector());
        }
    }

    public Optional<MultiBlockStructure<?>> getStructure(BreweryLocation location) {
        UUID worldUuid = location.worldUuid();
        Map<BreweryVector, MultiBlockStructure<?>> placedBreweryStructureMap = structures.getOrDefault(worldUuid, new HashMap<>());
        return Optional.ofNullable(placedBreweryStructureMap.get(location.toVector()));
    }

    public Set<MultiBlockStructure<?>> getStructures(Collection<BreweryLocation> locations) {
        Set<MultiBlockStructure<?>> breweryStructures = new HashSet<>();
        for (BreweryLocation location : locations) {
            getStructure(location).ifPresent(breweryStructures::add);
        }
        return breweryStructures;
    }

    public Optional<StructureHolder<?>> getHolder(BreweryLocation location) {
        UUID worldUuid = location.worldUuid();
        Map<BreweryVector, MultiBlockStructure<? extends StructureHolder<?>>> placedBreweryStructureMap = structures.getOrDefault(worldUuid, new HashMap<>());
        return Optional.ofNullable(placedBreweryStructureMap.get(location.toVector()))
                .map(MultiBlockStructure::getHolder);
    }

    public void unloadWorld(UUID worldUuid) {
        structures.remove(worldUuid);
    }

    public void clear() {
        structures.clear();
    }
}
