package dev.jsinco.brewery.recipes;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecipeRegistry<I, M> {


    private Map<String, Recipe<I, M>> recipes = new HashMap<>();
    private Map<String, RecipeResult<I, M>> defaultRecipes;
    private List<RecipeResult<I, M>> defaultRecipeList;

    private static final Random RANDOM = new Random();

    public void registerRecipes(@NotNull Map<String, Recipe<I, M>> recipes) {
        this.recipes = recipes;
    }

    public Optional<Recipe<I, M>> getRecipe(@NotNull String recipeName) {
        Preconditions.checkNotNull(recipeName);
        return Optional.ofNullable(recipes.get(recipeName));
    }

    public Collection<Recipe<I, M>> getRecipes() {
        return recipes.values();
    }

    public Optional<RecipeResult<I, M>> getDefaultRecipe(@NotNull String recipeName) {
        Preconditions.checkNotNull(recipeName);
        return Optional.ofNullable(defaultRecipes.get(recipeName));
    }

    public Collection<RecipeResult<I, M>> getDefaultRecipes() {
        return defaultRecipeList;
    }

    public RecipeResult<I, M> getRandomDefaultRecipe() {
        return defaultRecipeList.get(RANDOM.nextInt(defaultRecipeList.size()));
    }

    public void registerDefaultRecipes(@NotNull Map<String, RecipeResult<I, M>> defaultRecipes) {
        this.defaultRecipes = Preconditions.checkNotNull(defaultRecipes);
        this.defaultRecipeList = List.copyOf(defaultRecipes.values());
    }
}
