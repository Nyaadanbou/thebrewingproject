package dev.jsinco.brewery.recipes;

public interface DefaultRecipe<I, P> {

    I newBrewItem();

    void applyMeta(P meta);
}
