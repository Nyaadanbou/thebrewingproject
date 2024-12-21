package dev.jsinco.brewery;

import dev.jsinco.brewery.factories.RecipeFactory;
import dev.jsinco.brewery.recipes.ingredient.PluginIngredient;
import dev.jsinco.brewery.recipes.ingredient.external.OraxenPluginIngredient;
import dev.jsinco.brewery.structure.StructureRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class TheBrewingProject extends JavaPlugin {

    @Getter
    private static TheBrewingProject instance;
    @Getter
    private RecipeFactory recipeFactory;
    private StructureRegistry structureRegistry;

    @Override
    public void onLoad() {
        instance = this;
        this.recipeFactory = new RecipeFactory();
        this.structureRegistry = new StructureRegistry();
    }

    @Override
    public void onEnable() {
    }


    public void registerPluginIngredients() {
        PluginIngredient.registerPluginIngredient("Oraxen", OraxenPluginIngredient::new);
    }
}