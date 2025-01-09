package dev.jsinco.brewery.recipes;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecipeRegistry {


    private Map<String, Recipe> recipes;
    private Map<String, DefaultRecipe> defaultRecipes;
    private List<DefaultRecipe> defaultRecipeList;

    private static final Random RANDOM = new Random();

    public void registerRecipes(@NotNull Map<String, Recipe> recipes) {
        this.recipes = Objects.requireNonNull(recipes);
    }

    public Optional<Recipe> getRecipe(@NotNull String recipeName) {
        Objects.requireNonNull(recipeName);
        return Optional.ofNullable(recipes.get(recipeName));
    }

    public Collection<Recipe> getRecipes() {
        return recipes.values();
    }

    public Optional<DefaultRecipe> getDefaultRecipe(@NotNull String recipeName) {
        Objects.requireNonNull(recipeName);
        return Optional.ofNullable(defaultRecipes.get(recipeName));
    }

    public Collection<DefaultRecipe> getDefaultRecipes() {
        return defaultRecipeList;
    }

    public DefaultRecipe getRandomDefaultRecipe() {
        return defaultRecipeList.get(RANDOM.nextInt(defaultRecipeList.size()));
    }

    public void registerDefaultRecipes(@NotNull Map<String, DefaultRecipe> defaultRecipes) {
        this.defaultRecipes = Objects.requireNonNull(defaultRecipes);
        this.defaultRecipeList = List.copyOf(defaultRecipes.values());
    }
}
