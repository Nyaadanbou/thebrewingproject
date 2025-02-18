package dev.jsinco.brewery.recipes;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecipeRegistry<R, I, M> {


    private Map<String, Recipe<R, I>> recipes;
    private Map<String, DefaultRecipe<I, M>> defaultRecipes;
    private List<DefaultRecipe<I, M>> defaultRecipeList;

    private static final Random RANDOM = new Random();

    public void registerRecipes(@NotNull Map<String, Recipe<R, I>> recipes) {
        this.recipes = Objects.requireNonNull(recipes);
    }

    public Optional<Recipe> getRecipe(@NotNull String recipeName) {
        Objects.requireNonNull(recipeName);
        return Optional.ofNullable(recipes.get(recipeName));
    }

    public Collection<Recipe<R, I>> getRecipes() {
        return recipes.values();
    }

    public Optional<DefaultRecipe<I, M>> getDefaultRecipe(@NotNull String recipeName) {
        Objects.requireNonNull(recipeName);
        return Optional.ofNullable(defaultRecipes.get(recipeName));
    }

    public Collection<DefaultRecipe<I, M>> getDefaultRecipes() {
        return defaultRecipeList;
    }

    public DefaultRecipe<I, M> getRandomDefaultRecipe() {
        return defaultRecipeList.get(RANDOM.nextInt(defaultRecipeList.size()));
    }

    public void registerDefaultRecipes(@NotNull Map<String, DefaultRecipe<I, M>> defaultRecipes) {
        this.defaultRecipes = Objects.requireNonNull(defaultRecipes);
        this.defaultRecipeList = List.copyOf(defaultRecipes.values());
    }
}
