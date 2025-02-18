package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.SinglePositionStructure;
import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.util.*;

/**
 * Class which stores lists of all necessary objects
 * AKA. (Reduced)Recipes, Cauldrons, Barrels, BreweryPlayers
 */
public final class BreweryRegistry<C extends SinglePositionStructure, B> {

    private final Map<BreweryLocation, C> activeCauldrons = new HashMap<>();
    private final Set<B> openedBarrels = new HashSet<>();

    public Optional<C> getActiveCauldron(BreweryLocation position) {
        return Optional.ofNullable(activeCauldrons.get(position));
    }

    public void addActiveCauldron(C cauldron) {
        activeCauldrons.put(cauldron.position(), cauldron);
    }

    public void registerOpenedBarrel(B barrel) {
        openedBarrels.add(barrel);
    }

    public Collection<B> getOpenedBarrels() {
        return openedBarrels;
    }

    public void removeActiveCauldron(C cauldron) {
        activeCauldrons.remove(cauldron.position());
    }

    public void removeOpenedBarrel(B barrel) {
        openedBarrels.remove(barrel);
    }

    public Collection<C> getActiveCauldrons() {
        return activeCauldrons.values();
    }
}
