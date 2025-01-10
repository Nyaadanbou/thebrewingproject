package dev.jsinco.brewery;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BreweryRegistry;
import dev.jsinco.brewery.command.TestCommand;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.database.DatabaseDriver;
import dev.jsinco.brewery.listeners.BlockEventListener;
import dev.jsinco.brewery.listeners.PlayerEventListener;
import dev.jsinco.brewery.recipes.RecipeFactory;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.PluginIngredient;
import dev.jsinco.brewery.recipes.ingredient.external.OraxenPluginIngredient;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.structure.StructureReader;
import dev.jsinco.brewery.structure.StructureRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.stream.Stream;

public class TheBrewingProject extends JavaPlugin {

    @Getter
    private static TheBrewingProject instance;
    private StructureRegistry structureRegistry;
    private PlacedStructureRegistry placedStructureRegistry;
    @Getter
    private RecipeRegistry recipeRegistry;
    @Getter
    private BreweryRegistry breweryRegistry;
    @Getter
    private Database database;

    @Override
    public void onLoad() {
        instance = this;
        this.structureRegistry = new StructureRegistry();
        this.placedStructureRegistry = new PlacedStructureRegistry();
        this.breweryRegistry = new BreweryRegistry();

        Stream.of("/structures/small_barrel.json", "/structures/large_barrel.json")
                .map(string -> {
                    try {
                        return StructureReader.fromInternalResourceJson(string);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(structureRegistry::addStructures);
        this.recipeRegistry = new RecipeRegistry();
    }

    @Override
    public void onEnable() {
        this.database = new Database(DatabaseDriver.SQLITE);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this.structureRegistry, placedStructureRegistry), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this.placedStructureRegistry, this.breweryRegistry), this);
        Bukkit.getScheduler().runTaskTimer(this, this::updateBarrels, 0, 1);

        this.recipeRegistry.registerRecipes(RecipeFactory.readRecipes());
        this.recipeRegistry.registerDefaultRecipes(RecipeFactory.readDefaultRecipes());
        getCommand("test").setExecutor(new TestCommand());
    }

    private void updateBarrels() {
        breweryRegistry.getOpenedBarrels().forEach(Barrel::tick);
    }

    public void registerPluginIngredients() {
        PluginIngredient.registerPluginIngredient("Oraxen", OraxenPluginIngredient::new);
    }
}