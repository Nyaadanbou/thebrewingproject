package dev.jsinco.brewery.recipes;

import dev.jsinco.brewery.brew.Brew;

public interface RecipeResult<I, M> {

    I newBrewItem(BrewScore score, Brew brew);

    void applyMeta(BrewScore score, M meta, Brew brew);
}
