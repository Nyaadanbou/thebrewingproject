package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import dev.jsinco.brewery.util.vector.BreweryVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlacedStructureRegistry<S extends MultiBlockStructure> {

    private final Map<UUID, Map<BreweryVector, S>> structures = new HashMap<>();
    private final Map<UUID, Map<BreweryVector, Barrel>> holders = new HashMap<>();

    public void registerStructure(S placedBreweryStructure) {
        for (BreweryLocation location : placedBreweryStructure.positions()) {
            UUID worldUuid = location.worldUuid();
            structures.computeIfAbsent(worldUuid, ignored -> new HashMap<>()).put(location.toVector(), placedBreweryStructure);
        }
    }

    public void removeStructure(S placedBreweryStructure) {
        for (BreweryLocation location : placedBreweryStructure.positions()) {
            UUID worldUuid = location.worldUuid();
            Map<BreweryVector, S> placedBreweryStructureMap = structures.get(worldUuid);
            if (placedBreweryStructureMap == null) {
                // Assume single world structures
                return;
            }
            placedBreweryStructureMap.remove(location.toVector());
        }
    }

    public Optional<S> getStructure(BreweryLocation location) {
        UUID worldUuid = location.worldUuid();
        Map<BreweryVector, S> placedBreweryStructureMap = structures.getOrDefault(worldUuid, new HashMap<>());
        return Optional.ofNullable(placedBreweryStructureMap.get(location.toVector()));
    }

    public Set<S> getStructures(Collection<BreweryLocation> locations) {
        Set<S> breweryStructures = new HashSet<>();
        for (BreweryLocation location : locations) {
            getStructure(location).ifPresent(breweryStructures::add);
        }
        return breweryStructures;
    }

    public void registerPosition(@NotNull BreweryLocation location, @NotNull Barrel behaviorHolder) {
        holders.computeIfAbsent(location.worldUuid(), ignored -> new HashMap<>()).put(location.toVector(), behaviorHolder);
    }

    public Optional<Barrel> getHolder(@NotNull BreweryLocation location) {
        return Optional.ofNullable(holders.getOrDefault(location.worldUuid(), new HashMap<>()).get(location.toVector()));
    }

    public void removePosition(BreweryLocation location) {
        holders.getOrDefault(location.worldUuid(), new HashMap<>()).remove(location.toVector());
    }

    public void unloadWorld(UUID worldUuid) {
        holders.remove(worldUuid);
        structures.remove(worldUuid);
    }
}
