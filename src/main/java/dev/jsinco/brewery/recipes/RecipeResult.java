package dev.jsinco.brewery.recipes;

import dev.jsinco.brewery.brews.Brew;

public interface RecipeResult<I, M> {

    I newBrewItem(BrewScore score, Brew<I> brew);

    void applyMeta(BrewScore score, M meta, Brew<I> brew);
}
