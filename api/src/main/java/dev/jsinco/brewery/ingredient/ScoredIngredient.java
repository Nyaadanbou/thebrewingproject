package dev.jsinco.brewery.ingredient;

public interface ScoredIngredient extends Ingredient {

    double score();

    Ingredient baseIngredient();
}
