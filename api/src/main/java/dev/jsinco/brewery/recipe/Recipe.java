package dev.jsinco.brewery.recipe;

import dev.jsinco.brewery.brew.BrewingStep;

import java.util.List;

public interface Recipe<I> {

    String getRecipeName();

    int getBrewDifficulty();

    List<BrewingStep> getSteps();

    RecipeResult<I> getRecipeResult();
}
