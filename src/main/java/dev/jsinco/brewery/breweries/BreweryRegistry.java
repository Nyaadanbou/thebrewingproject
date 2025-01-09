package dev.jsinco.brewery.breweries;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

/**
 * Class which stores lists of all necessary objects
 * AKA. (Reduced)Recipes, Cauldrons, Barrels, BreweryPlayers
 */
public final class BreweryRegistry {

    private final Map<Location, Cauldron> activeCauldrons = new HashMap<>();
    private final Map<UUID, Barrel> openedBarrels = new HashMap<>();

    public Optional<Cauldron> getActiveCauldron(Block block) {
        return Optional.ofNullable(activeCauldrons.get(block.getLocation()));
    }

    public void addActiveCauldron(Cauldron cauldron) {
        activeCauldrons.put(cauldron.getBlock().getLocation(), cauldron);
    }

    public void registerOpenedBarrel(UUID playerUuid, Barrel barrel) {
        openedBarrels.put(playerUuid, barrel);
    }

    public Optional<Barrel> getOpenedBarrel(UUID playerUuid) {
        return Optional.ofNullable(openedBarrels.get(playerUuid));
    }

    public Collection<Barrel> getOpenedBarrels() {
        return openedBarrels.values();
    }

    public void removeActiveCauldron(Cauldron cauldron) {
        activeCauldrons.remove(cauldron.getBlock().getLocation());
    }

    public void removeOpenedBarrel(UUID playerUuid) {
        openedBarrels.remove(playerUuid);
    }
}
