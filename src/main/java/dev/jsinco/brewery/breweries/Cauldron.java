package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.structure.SinglePositionStructure;

import java.util.Map;

public interface Cauldron<I> extends Tickable, SinglePositionStructure {

    long brewStart();

    Map<Ingredient<I>, Integer> ingredients();
}
