package dev.jsinco.brewery.bukkit;

import dev.jsinco.brewery.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.BukkitBarrel;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldron;
import dev.jsinco.brewery.bukkit.command.TestCommand;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.ingredient.PluginIngredient;
import dev.jsinco.brewery.bukkit.ingredient.external.OraxenPluginIngredient;
import dev.jsinco.brewery.bukkit.listeners.BlockEventListener;
import dev.jsinco.brewery.bukkit.listeners.PlayerEventListener;
import dev.jsinco.brewery.bukkit.listeners.WorldEventListener;
import dev.jsinco.brewery.bukkit.recipe.BukkitRecipeResultReader;
import dev.jsinco.brewery.bukkit.recipe.DefaultRecipeReader;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.structure.StructureReader;
import dev.jsinco.brewery.bukkit.structure.StructureRegistry;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.database.DatabaseDriver;
import dev.jsinco.brewery.recipes.RecipeReader;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

public class TheBrewingProject extends JavaPlugin {

    @Getter
    private static TheBrewingProject instance;
    @Getter
    private StructureRegistry structureRegistry;
    private PlacedStructureRegistry<PlacedBreweryStructure> placedStructureRegistry;
    @Getter
    private RecipeRegistry<RecipeResult, ItemStack, PotionMeta> recipeRegistry;
    @Getter
    private BreweryRegistry<BukkitCauldron, BukkitBarrel> breweryRegistry;
    @Getter
    private Database database;

    @Override
    public void onLoad() {
        instance = this;
        this.structureRegistry = new StructureRegistry();
        this.placedStructureRegistry = new PlacedStructureRegistry<>();
        this.breweryRegistry = new BreweryRegistry<>();

        Stream.of("/structures/small_barrel.json", "/structures/large_barrel.json")
                .map(string -> {
                    try {
                        return StructureReader.fromInternalResourceJson(string);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(structureRegistry::addStructures);
        this.recipeRegistry = new RecipeRegistry<>();
    }

    @Override
    public void onEnable() {
        saveResources();
        this.database = new Database(DatabaseDriver.SQLITE);
        try {
            database.init(this.getDataFolder());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e); // Hard exit if any issues here
        }
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this.structureRegistry, placedStructureRegistry, this.database, this.breweryRegistry), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this.placedStructureRegistry, this.breweryRegistry, this.database), this);
        WorldEventListener worldEventListener = new WorldEventListener(this.database, this.placedStructureRegistry);
        worldEventListener.init();
        Bukkit.getPluginManager().registerEvents(worldEventListener, this);
        Bukkit.getScheduler().runTaskTimer(this, this::updateBarrels, 0, 1);
        RecipeReader<RecipeResult, ItemStack> recipeReader = new RecipeReader<>(this.getDataFolder(), new BukkitRecipeResultReader(), BukkitIngredientManager.INSTANCE);

        this.recipeRegistry.registerRecipes(recipeReader.readRecipes());
        this.recipeRegistry.registerDefaultRecipes(DefaultRecipeReader.readDefaultRecipes(this.getDataFolder()));
        getCommand("test").setExecutor(new TestCommand());
    }

    private void saveResources() {
        this.saveResource("recipes.yml", false);
        this.saveResource("ingredients.yml", false);
    }

    private void updateBarrels() {
        breweryRegistry.getOpenedBarrels().forEach(BukkitBarrel::tick);
        breweryRegistry.getActiveCauldrons().forEach(BukkitCauldron::tick);
    }

    public void registerPluginIngredients() {
        PluginIngredient.registerPluginIngredient("Oraxen", OraxenPluginIngredient::new);
    }
}