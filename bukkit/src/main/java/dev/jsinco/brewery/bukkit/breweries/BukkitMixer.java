package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.breweries.Mixer;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BukkitMixer implements Mixer {

    private final BreweryLocation location;
    @Getter
    private Brew brew;

    public BukkitMixer(BreweryLocation location, Brew brew) {
        this.location = location;
        this.brew = brew;
    }

    @Override
    public void tick() {

    }

    public void addIngredient(Ingredient ingredient) {
        long time = TheBrewingProject.getInstance().getTime();
        this.brew = brew.withLastStep(
                BrewingStep.Mix.class,
                mix -> {
                    Map<Ingredient, Integer> newIngredients = new HashMap<>(mix.ingredients());
                    BukkitIngredientManager.INSTANCE.insertIngredientIntoMap(newIngredients, new Pair<>(ingredient, 1));
                    return mix.withIngredients((Map<Ingredient, Integer>) newIngredients);
                },
                () -> new BrewingStep.Mix(new Interval(time, time), Map.of())
        );
    }

    @Override
    public BreweryLocation position() {
        return location;
    }
}
