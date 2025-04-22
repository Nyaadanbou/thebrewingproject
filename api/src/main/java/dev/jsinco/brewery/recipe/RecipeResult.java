package dev.jsinco.brewery.recipe;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewScore;

public interface RecipeResult<I> {

    I newBrewItem(BrewScore score, Brew brew, Brew.State state);
}
