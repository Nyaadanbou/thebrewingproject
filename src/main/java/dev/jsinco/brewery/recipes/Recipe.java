package dev.jsinco.brewery.recipes;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Recipe object with fewer attributes only used for identifying which recipe is being brewed
 */
@Getter
public class Recipe {

    // TODO: re-add specific heat sources

    protected final String recipeName;

    // Used for identifying and for particle effects while brewing in a cauldron
    protected final Map<Ingredient, Integer> ingredients;
    protected final int brewTime;
    protected final int brewDifficulty;
    protected final CauldronType cauldronType;

    // Used for aging
    protected final BarrelType barrelType;
    protected final int agingYears;

    // Used for distilling
    protected final int distillRuns;
    protected final int distillTime;

    // End product
    @NotNull
    private final RecipeResult recipeResult;


    private Recipe(String recipeName, Map<Ingredient, Integer> ingredients, int brewTime, int brewDifficulty,
                   CauldronType cauldronType, BarrelType barrelType, int agingYears, int distillRuns, int distillTime, @NotNull RecipeResult recipeResult) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.brewTime = brewTime;
        this.brewDifficulty = brewDifficulty;
        this.cauldronType = cauldronType == null ? CauldronType.WATER : cauldronType;
        this.barrelType = barrelType == null ? BarrelType.ANY : barrelType;
        this.agingYears = agingYears;
        this.distillRuns = distillRuns;
        this.distillTime = distillTime;
        this.recipeResult = recipeResult;
    }

    public static class Builder {
        private final String recipeName;
        private Map<Ingredient, Integer> ingredients = new HashMap<>();
        private int brewTime = 1;
        private int brewDifficulty = 1;
        private CauldronType cauldronType = CauldronType.WATER;
        private BarrelType barrelType = BarrelType.ANY;
        private int agingYears = 0;
        private int distillRuns = 0;
        private int distillTime = 30;
        private RecipeResult recipeResult;

        public Builder(String recipeName) {
            this.recipeName = recipeName;
        }

        public Builder ingredients(Map<Ingredient, Integer> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Builder brewTime(int brewTime) {
            this.brewTime = brewTime;
            return this;
        }

        public Builder brewDifficulty(int brewDifficulty) {
            this.brewDifficulty = brewDifficulty;
            return this;
        }

        public Builder cauldronType(CauldronType cauldronType) {
            this.cauldronType = cauldronType;
            return this;
        }

        public Builder barrelType(BarrelType barrelType) {
            this.barrelType = barrelType;
            return this;
        }

        public Builder agingYears(int agingYears) {
            this.agingYears = agingYears;
            return this;
        }

        public Builder distillRuns(int distillRuns) {
            this.distillRuns = distillRuns;
            return this;
        }

        public Builder distillTime(int distillTime) {
            this.distillTime = distillTime;
            return this;
        }

        public Builder recipeResult(@NotNull RecipeResult recipeResult) {
            this.recipeResult = Objects.requireNonNull(recipeResult);
            return this;
        }

        public Recipe build() {
            Objects.requireNonNull(recipeResult);
            if (ingredients.isEmpty()) {
                throw new IllegalStateException("Ingredients should not be empty");
            }
            return new Recipe(recipeName, ingredients, brewTime, brewDifficulty, cauldronType, barrelType, agingYears, distillRuns, distillTime, recipeResult);
        }
    }
}
