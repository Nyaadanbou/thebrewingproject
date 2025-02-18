package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.Moment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record Brew<I>(@Nullable Moment brewTime, @NotNull Map<Ingredient<I>, Integer> ingredients,
                      @Nullable Moment aging, int distillRuns, @Nullable CauldronType cauldronType,
                      @Nullable BarrelType barrelType) {

    public static <I> boolean sameValuesForAging(@NotNull Brew<I> brew1, Brew<I> brew2) {
        if (brew2 == null) {
            return false;
        }
        if (((Interval) brew1.aging).start() != ((Interval) brew2.aging).start()) {
            return false;
        }
        if (brew1.brewTime.moment() != brew2.brewTime.moment()) {
            return false;
        }
        if (!brew1.ingredients.equals(brew2.ingredients)) {
            return false;
        }
        if (brew1.distillRuns != brew2.distillRuns) {
            return false;
        }
        return brew1.cauldronType == brew2.cauldronType && brew1.barrelType == brew2.barrelType;
    }

    public Brew<I> withCauldronTime(Interval interval) {
        return new Brew<>(interval, ingredients, aging, distillRuns, cauldronType, barrelType);
    }

    public Brew<I> withIngredients(Map<Ingredient<I>, Integer> ingredients) {
        return new Brew<>(brewTime, Map.copyOf(ingredients), aging, distillRuns, cauldronType, barrelType);
    }

    public Brew<I> withAging(Interval interval) {
        return new Brew<>(brewTime, ingredients, interval, distillRuns, cauldronType, barrelType);
    }

    public Brew<I> withDistillAmount(int amount) {
        return new Brew<>(brewTime, ingredients, aging, amount, cauldronType, barrelType);
    }

    public Brew<I> withCauldronType(CauldronType type) {
        return new Brew<>(brewTime, ingredients, aging, distillRuns, type, barrelType);
    }

    public Brew<I> withBarrelType(BarrelType type) {
        return new Brew<>(brewTime, ingredients, aging, distillRuns, cauldronType, type);
    }


    public <R> Optional<Recipe<R, I>> closestRecipe(RecipeRegistry<R, I, ?> registry) {
        double bestScore = 0;
        Recipe<R,I> bestMatch = null;
        for (Recipe<R, I> recipe : registry.getRecipes()) {
            // Don't even bother checking recipes that don't have the same amount of ingredients
            if (this.ingredients.size() != recipe.getIngredients().size()
                    || !this.ingredients.keySet().equals(recipe.getIngredients().keySet())) {
                continue;
            }
            double score = evaluateRecipe(recipe);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }
        return Optional.ofNullable(bestMatch);
    }

    private double evaluateRecipe(Recipe<?, I> recipe) {
        double ingredientScore = getIngredientScore(recipe.getIngredients(), this.ingredients);
        double cauldronTimeScore = 1;
        if (brewTime != null) {
            if (recipe.getBrewTime() == 0 && brewTime.minutes() > 0) {
                cauldronTimeScore = 0;
            } else {
                cauldronTimeScore = getNearbyValueScore(recipe.getBrewTime(), brewTime.minutes());
            }
        }
        double agingTimeScore = 1;
        if (aging != null) {
            if (recipe.getAgingYears() == 0 && aging.agingYears() > 0) {
                agingTimeScore = 0;
            } else {
                agingTimeScore = getNearbyValueScore(recipe.getAgingYears(), aging.agingYears());
            }
        }
        double cauldronTypeScore = 1;
        if (cauldronType != null) {
            if (!cauldronType.equals(recipe.getCauldronType())) {
                cauldronTypeScore = 0.9;
            }
        }
        double barrelTypeScore = 1;
        if (barrelType != null) {
            if (!barrelType.equals(recipe.getBarrelType())) {
                barrelTypeScore = 0.9;
            }
        }
        return ingredientScore * cauldronTimeScore * agingTimeScore * cauldronTypeScore * barrelTypeScore;
    }

    private double getNearbyValueScore(long expected, long value) {
        double sigmoid = 1D / (1D + Math.exp((double) (expected - value) / expected));
        return sigmoid * (1D - sigmoid) * 4D;
    }

    private double getIngredientScore(Map<Ingredient<I>, Integer> target, Map<Ingredient<I>, Integer> actual) {
        double score = 1;
        for (Map.Entry<Ingredient<I>, Integer> targetEntry : target.entrySet()) {
            Integer actualAmount = actual.get(targetEntry.getKey());
            if (actualAmount == null) {
                return 0;
            }
            score *= getNearbyValueScore(targetEntry.getValue(), actualAmount);
        }
        return score;
    }

    public Optional<PotionQuality> quality(Recipe<?, I> recipe) {
        double score = evaluateRecipe(recipe);
        double scoreWithDifficulty;
        // Avoid extreme point, log(0) is minus infinity
        if (recipe.getBrewDifficulty() == 0) {
            return Optional.of(PotionQuality.EXCELLENT);
        }
        // Avoid extreme point in calculation (can not divide by 0)
        if (recipe.getBrewDifficulty() == 1) {
            scoreWithDifficulty = score;
        } else {
            double logBrewDifficulty = Math.log(recipe.getBrewDifficulty());
            scoreWithDifficulty = (Math.exp(score * logBrewDifficulty) / Math.exp(logBrewDifficulty) - 1D / recipe.getBrewDifficulty()) / (1 - 1D / recipe.getBrewDifficulty());
        }
        if (scoreWithDifficulty > 0.9) {
            return Optional.of(PotionQuality.EXCELLENT);
        }
        if (scoreWithDifficulty > 0.5) {
            return Optional.of(PotionQuality.GOOD);
        }
        if (scoreWithDifficulty > 0.3) {
            return Optional.of(PotionQuality.BAD);
        }
        return Optional.empty();
    }

    public boolean hasCompletedRecipe(Recipe<?, I> recipe) {
        if (!recipe.getIngredients().keySet().equals(ingredients.keySet())) {
            return false;
        }
        if (recipe.getBrewTime() > 0 && (brewTime == null || brewTime.minutes() < 1)) {
            return false;
        }
        if (recipe.getAgingYears() > 0 && (aging == null || aging.agingYears() < 1)) {
            return false;
        }
        return recipe.getDistillRuns() <= 0 || distillRuns >= 1;
    }
}
