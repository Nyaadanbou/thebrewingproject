package dev.jsinco.brewery.recipes;

import dev.jsinco.brewery.brew.Brew;

public interface RecipeResult<I> {

    I newBrewItem(BrewScore score, Brew brew, Brew.State state);
}
