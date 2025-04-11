package dev.jsinco.brewery.recipes;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecipeRegistry<I> {


    private Map<String, Recipe<I>> recipes = new HashMap<>();
    private Map<String, RecipeResult<I>> defaultRecipes;
    private List<RecipeResult<I>> defaultRecipeList;

    private static final Random RANDOM = new Random();

    public void registerRecipes(@NotNull Map<String, Recipe<I>> recipes) {
        this.recipes = recipes;
    }

    public Optional<Recipe<I>> getRecipe(@NotNull String recipeName) {
        Preconditions.checkNotNull(recipeName);
        return Optional.ofNullable(recipes.get(recipeName));
    }

    public Collection<Recipe<I>> getRecipes() {
        return recipes.values();
    }

    public Optional<RecipeResult<I>> getDefaultRecipe(@NotNull String recipeName) {
        Preconditions.checkNotNull(recipeName);
        return Optional.ofNullable(defaultRecipes.get(recipeName));
    }

    public Collection<RecipeResult<I>> getDefaultRecipes() {
        return defaultRecipeList;
    }

    public RecipeResult<I> getRandomDefaultRecipe() {
        return defaultRecipeList.get(RANDOM.nextInt(defaultRecipeList.size()));
    }

    public void registerDefaultRecipes(@NotNull Map<String, RecipeResult<I>> defaultRecipes) {
        this.defaultRecipes = Preconditions.checkNotNull(defaultRecipes);
        this.defaultRecipeList = List.copyOf(defaultRecipes.values());
    }
}
