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
    private final Set<Barrel> openedBarrels = new HashSet<>();

    public Optional<Cauldron> getActiveCauldron(Block block) {
        return Optional.ofNullable(activeCauldrons.get(block.getLocation()));
    }

    public void addActiveCauldron(Cauldron cauldron) {
        activeCauldrons.put(cauldron.getBlock().getLocation(), cauldron);
    }

    public void registerOpenedBarrel(Barrel barrel) {
        openedBarrels.add(barrel);
    }

    public Collection<Barrel> getOpenedBarrels() {
        return openedBarrels;
    }

    public void removeActiveCauldron(Cauldron cauldron) {
        activeCauldrons.remove(cauldron.getBlock().getLocation());
    }

    public void removeOpenedBarrel(Barrel barrel) {
        openedBarrels.remove(barrel);
    }
}
