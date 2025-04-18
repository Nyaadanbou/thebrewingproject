package dev.jsinco.brewery.recipes.ingredient;

public interface ScoredIngredient extends Ingredient {

    double score();

    Ingredient baseIngredient();
}
