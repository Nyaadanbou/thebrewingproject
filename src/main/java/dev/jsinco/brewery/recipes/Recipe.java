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
public class Recipe<R, I> {

    // TODO: re-add specific heat sources

    protected final String recipeName;

    // Used for identifying and for particle effects while brewing in a cauldron
    protected final Map<Ingredient<I>, Integer> ingredients;
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
    private final R recipeResult;


    private Recipe(String recipeName, Map<Ingredient<I>, Integer> ingredients, int brewTime, int brewDifficulty,
                   CauldronType cauldronType, BarrelType barrelType, int agingYears, int distillRuns, int distillTime, @NotNull R recipeResult) {
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

    public static class Builder<R, I> {
        private final String recipeName;
        private Map<Ingredient<I>, Integer> ingredients = new HashMap<>();
        private int brewTime = 1;
        private int brewDifficulty = 1;
        private CauldronType cauldronType = CauldronType.WATER;
        private BarrelType barrelType = BarrelType.ANY;
        private int agingYears = 0;
        private int distillRuns = 0;
        private int distillTime = 30;
        private R recipeResult;

        public Builder(String recipeName) {
            this.recipeName = recipeName;
        }

        public Builder<R, I> ingredients(Map<Ingredient<I>, Integer> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Builder<R, I> brewTime(int brewTime) {
            this.brewTime = brewTime;
            return this;
        }

        public Builder<R, I> brewDifficulty(int brewDifficulty) {
            this.brewDifficulty = brewDifficulty;
            return this;
        }

        public Builder<R, I> cauldronType(CauldronType cauldronType) {
            this.cauldronType = cauldronType;
            return this;
        }

        public Builder<R, I> barrelType(BarrelType barrelType) {
            this.barrelType = barrelType;
            return this;
        }

        public Builder<R, I> agingYears(int agingYears) {
            this.agingYears = agingYears;
            return this;
        }

        public Builder<R, I> distillRuns(int distillRuns) {
            this.distillRuns = distillRuns;
            return this;
        }

        public Builder<R, I> distillTime(int distillTime) {
            this.distillTime = distillTime;
            return this;
        }

        public Builder<R, I> recipeResult(@NotNull R recipeResult) {
            this.recipeResult = Objects.requireNonNull(recipeResult);
            return this;
        }

        public Recipe<R, I> build() {
            Objects.requireNonNull(recipeResult);
            if (ingredients.isEmpty()) {
                throw new IllegalStateException("Ingredients should not be empty");
            }
            return new Recipe<>(recipeName, ingredients, brewTime, brewDifficulty, cauldronType, barrelType, agingYears, distillRuns, distillTime, recipeResult);
        }
    }
}
