package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScore;
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

    public Brew<I> withCauldronTime(Moment interval) {
        return new Brew<>(interval, ingredients, aging, distillRuns, cauldronType, barrelType);
    }

    public Brew<I> withIngredients(Map<Ingredient<I>, Integer> ingredients) {
        return new Brew<>(brewTime, Map.copyOf(ingredients), aging, distillRuns, cauldronType, barrelType);
    }

    public Brew<I> withAging(Moment interval) {
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


    public <M> Optional<Recipe<I, M>> closestRecipe(RecipeRegistry<I, M> registry) {
        double bestScore = 0;
        Recipe<I, M> bestMatch = null;
        for (Recipe<I, M> recipe : registry.getRecipes()) {
            // Don't even bother checking recipes that don't have the same amount of ingredients
            if (this.ingredients.size() != recipe.getIngredients().size()
                    || !this.ingredients.keySet().equals(recipe.getIngredients().keySet())) {
                continue;
            }
            double score = score(recipe).rawScore();
            if (score > bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }
        return Optional.ofNullable(bestMatch);
    }

    private double getNearbyValueScore(long expected, long value) {
        double sigmoid = 1D / (1D + Math.exp((double) (expected - value) / expected * 5));
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

    public @NotNull BrewScore score(Recipe<I, ?> recipe) {
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
            if (!barrelType.equals(recipe.getBarrelType()) && recipe.getBarrelType() != BarrelType.ANY) {
                barrelTypeScore = 0.9;
            }
        }
        double distillRunsScore = 1;
        if (distillRuns > 0) {
            if (recipe.getDistillRuns() == 0) {
                distillRunsScore = 0;
            } else {
                distillRunsScore = getNearbyValueScore(recipe.getDistillRuns(), distillRuns);
            }
        }
        return new BrewScore(ingredientScore, cauldronTimeScore, distillRunsScore, agingTimeScore, cauldronTypeScore, barrelTypeScore, recipe.getBrewDifficulty());
    }

    public Optional<BrewQuality> quality(Recipe<I, ?> recipe) {
        return Optional.ofNullable(score(recipe).brewQuality());
    }

    public boolean hasCompletedRecipe(Recipe<I, ?> recipe) {
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
