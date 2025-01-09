package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.recipes.Recipe;
import lombok.Getter;

import java.util.*;

/**
 * Class which stores lists of all necessary objects
 * AKA. (Reduced)Recipes, Cauldrons, Barrels, BreweryPlayers
 */
// TODO: change to instantiated rather than static class
public final class BreweryRegistry {

    @Getter
    private static final List<Recipe> reducedRecipes = new ArrayList<>();
    @Getter
    private static final List<Cauldron> activeCauldrons = new ArrayList<>();
    @Getter
    private static final Map<UUID, Barrel> openedBarrels = new HashMap<>();
}
