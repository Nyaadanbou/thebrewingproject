package dev.jsinco.brewery.recipes;

import org.simpleyaml.configuration.ConfigurationSection;

public interface RecipeResultReader<R> {

    R readRecipeResult(ConfigurationSection configurationSection);
}
