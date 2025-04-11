package dev.jsinco.brewery.recipes;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.brew.BrewingStep;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Recipe object with fewer attributes only used for identifying which recipe is being brewed
 */
@Getter
public class Recipe<I> {

    private final String recipeName;

    private final int brewDifficulty;
    @NotNull
    private final List<BrewingStep> steps;

    // End product
    @NotNull
    private final RecipeResult<I> recipeResult;


    private Recipe(String recipeName, int brewDifficulty, List<BrewingStep> steps,
                   @NotNull RecipeResult<I> recipeResult) {
        this.recipeName = recipeName;
        this.brewDifficulty = brewDifficulty;
        this.steps = steps;
        this.recipeResult = recipeResult;
    }

    public static class Builder<I> {
        private final String recipeName;
        private int brewDifficulty = 1;
        private RecipeResult<I> recipeResult;
        private List<BrewingStep> steps;

        public Builder(String recipeName) {
            this.recipeName = recipeName;
        }

        public Builder<I> brewDifficulty(int brewDifficulty) {
            this.brewDifficulty = brewDifficulty;
            return this;
        }

        public Builder<I> recipeResult(@NotNull RecipeResult<I> recipeResult) {
            this.recipeResult = Preconditions.checkNotNull(recipeResult);
            return this;
        }

        public Builder<I> steps(@NotNull List<BrewingStep> steps) {
            this.steps = Preconditions.checkNotNull(steps);
            return this;
        }

        public Recipe<I> build() {
            Preconditions.checkNotNull(recipeResult);
            Preconditions.checkNotNull(steps);
            if (steps.isEmpty()) {
                throw new IllegalStateException("Steps should not be empty");
            }
            return new Recipe<>(recipeName, brewDifficulty, steps, recipeResult);
        }
    }
}
