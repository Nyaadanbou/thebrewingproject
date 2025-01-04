package dev.jsinco.brewery.structure;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;

import java.util.*;

public class PlacedStructureRegistry {

    private final Map<UUID, Map<BlockVector, PlacedBreweryStructure>> structures = new HashMap<>();

    public void addStructure(PlacedBreweryStructure placedBreweryStructure) {
        for (Location location : placedBreweryStructure.getPositions()) {
            UUID worldUuid = location.getWorld().getUID();
            structures.computeIfAbsent(worldUuid, ignored -> new HashMap<>()).put(location.toVector().toBlockVector(), placedBreweryStructure);
        }
    }

    public void removeStructure(PlacedBreweryStructure placedBreweryStructure) {
        for (Location location : placedBreweryStructure.getPositions()) {
            UUID worldUuid = location.getWorld().getUID();
            Map<BlockVector, PlacedBreweryStructure> placedBreweryStructureMap = structures.get(worldUuid);
            if (placedBreweryStructureMap == null) {
                // Assume single world structures
                return;
            }
            placedBreweryStructureMap.remove(location.toVector().toBlockVector());
        }
    }

    public Optional<PlacedBreweryStructure> getStructure(Location location) {
        UUID worldUuid = location.getWorld().getUID();
        Map<BlockVector, PlacedBreweryStructure> placedBreweryStructureMap = structures.getOrDefault(worldUuid, new HashMap<>());
        return Optional.ofNullable(placedBreweryStructureMap.get(location.toVector().toBlockVector()));
    }

    public Set<PlacedBreweryStructure> getStructures(Collection<Location> locations) {
        Set<PlacedBreweryStructure> breweryStructures = new HashSet<>();
        for (Location location : locations) {
            getStructure(location).ifPresent(breweryStructures::add);
        }
        return breweryStructures;
    }
}
