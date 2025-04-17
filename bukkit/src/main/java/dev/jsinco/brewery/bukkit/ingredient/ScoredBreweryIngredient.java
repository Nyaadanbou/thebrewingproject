package dev.jsinco.brewery.bukkit.ingredient;

import dev.jsinco.brewery.util.BreweryKey;
import lombok.Getter;

public class ScoredBreweryIngredient extends BreweryIngredient {

    @Getter
    private final double score;

    public ScoredBreweryIngredient(BreweryKey ingredientKey, double score) {
        super(ingredientKey);
        this.score = score;
    }

    @Override
    public String displayName() {
        return super.displayName() + "-" + score;
    }
}
