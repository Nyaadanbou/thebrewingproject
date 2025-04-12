package dev.jsinco.brewery.brew;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import dev.jsinco.brewery.recipes.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScore;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Brew {

    @Getter
    private final List<BrewingStep> steps;
    public static final Serializer SERIALIZER = new Serializer();

    public Brew(BrewingStep.Cook cook) {
        this(List.of(cook));
    }

    public Brew(BrewingStep.Mix mix) {
        this(List.of(mix));
    }

    public Brew(@NotNull List<BrewingStep> steps) {
        Preconditions.checkArgument(!steps.isEmpty(), "Steps cannot be empty");
        this.steps = steps;
    }

    public Brew withStep(BrewingStep step) {
        return new Brew(Stream.concat(steps.stream(), Stream.of(step)).toList());
    }

    public Brew witModifiedLastStep(Function<BrewingStep, BrewingStep> modifier) {
        BrewingStep newStep = modifier.apply(steps.getLast());
        return new Brew(Stream.concat(
                steps.subList(0, steps.size() - 1).stream(),
                Stream.of(newStep)).toList()
        );
    }

    public <B extends BrewingStep> Brew withLastStep(Class<B> bClass, Function<B, B> modifier, Supplier<B> stepSupplier) {
        if (bClass.isInstance(lastStep())) {
            BrewingStep newStep = modifier.apply(bClass.cast(lastStep()));
            return new Brew(Stream.concat(
                    steps.subList(0, steps.size() - 1).stream(),
                    Stream.of(newStep)).toList()
            );
        }
        return withStep(stepSupplier.get());
    }

    public <I> Optional<Recipe<I>> closestRecipe(RecipeRegistry<I> registry) {
        double bestScore = 0;
        Recipe<I> bestMatch = null;
        for (Recipe<I> recipe : registry.getRecipes()) {
            double score = score(recipe).rawScore();
            if (score > bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }
        return Optional.ofNullable(bestMatch);
    }

    public @NotNull BrewScore score(Recipe<?> recipe) {
        List<BrewingStep> recipeSteps = recipe.getSteps();
        List<Double> scores = new ArrayList<>();
        if (steps.size() > recipeSteps.size()) {
            return BrewScore.NONE;
        }
        for (int i = 0; i < steps.size(); i++) {
            BrewingStep recipeStep = recipeSteps.get(i);
            scores.add(recipeStep.proximity(steps.get(i)));
        }
        boolean completed = steps.size() == recipeSteps.size();
        BrewScore brewScore = new BrewScore(scores, completed, recipe.getBrewDifficulty());
        if (brewScore.brewQuality() == null) {
            scores.removeLast();
            scores.add(recipeSteps.get(steps.size() - 1).maximumScore(steps.getLast()));
            return new BrewScore(scores, false, recipe.getBrewDifficulty());
        }
        return brewScore;
    }

    public Optional<BrewQuality> quality(Recipe<?> recipe) {
        return Optional.ofNullable(score(recipe).brewQuality());
    }

    public BrewingStep lastStep() {
        return steps.getLast();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Brew brew = (Brew) other;
        return steps.equals(brew.steps);
    }

    public static class Serializer {

        public JsonArray serialize(Brew brew) {
            JsonArray array = new JsonArray();
            for (BrewingStep step : brew.steps) {
                array.add(BrewingStep.SERIALIZER.serialize(step));
            }
            return array;
        }

        public Brew deserialize(JsonArray jsonArray, IngredientManager<?> ingredientManager) {
            return new Brew(jsonArray.asList().stream().map(jsonElement -> BrewingStep.SERIALIZER.deserialize(jsonElement, ingredientManager)).toList());
        }
    }
}
