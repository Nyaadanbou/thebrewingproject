package dev.jsinco.brewery.recipes;

import org.simpleyaml.configuration.ConfigurationSection;

public interface RecipeResultReader<I, M> {

    RecipeResult<I, M> readRecipeResult(ConfigurationSection configurationSection);
}
