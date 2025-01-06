package dev.jsinco.brewery;

import dev.jsinco.brewery.factories.RecipeFactory;
import dev.jsinco.brewery.listeners.BlockEventListener;
import dev.jsinco.brewery.listeners.PlayerEventListener;
import dev.jsinco.brewery.objects.Barrel;
import dev.jsinco.brewery.objects.ObjectRegistry;
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
    @Getter
    private RecipeFactory recipeFactory;
    private StructureRegistry structureRegistry;
    private PlacedStructureRegistry placedStructureRegistry;

    @Override
    public void onLoad() {
        instance = this;
        this.recipeFactory = new RecipeFactory();
        this.structureRegistry = new StructureRegistry();
        this.placedStructureRegistry = new PlacedStructureRegistry();

        Stream.of("/structures/small_barrel.json", "/structures/large_barrel.json")
                .map(string -> {
                    try {
                        return StructureReader.fromInternalResourceJson(string);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(structureRegistry::addStructures);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this.structureRegistry, placedStructureRegistry), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this.placedStructureRegistry), this);
        Bukkit.getScheduler().runTaskTimer(this, this::updateBarrels, 0, 1);
    }

    private void updateBarrels() {
        ObjectRegistry.getOpenedBarrels().values().forEach(Barrel::tick);
    }

    public void registerPluginIngredients() {
        PluginIngredient.registerPluginIngredient("Oraxen", OraxenPluginIngredient::new);
    }
}