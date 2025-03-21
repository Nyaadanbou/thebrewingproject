package dev.jsinco.brewery.bukkit;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldron;
import dev.jsinco.brewery.bukkit.command.TestCommand;
import dev.jsinco.brewery.bukkit.effect.DrunkEventAction;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.ingredient.PluginIngredient;
import dev.jsinco.brewery.bukkit.ingredient.external.OraxenPluginIngredient;
import dev.jsinco.brewery.bukkit.listeners.BlockEventListener;
import dev.jsinco.brewery.bukkit.listeners.InventoryEventListener;
import dev.jsinco.brewery.bukkit.listeners.PlayerEventListener;
import dev.jsinco.brewery.bukkit.listeners.WorldEventListener;
import dev.jsinco.brewery.bukkit.recipe.BukkitRecipeResultReader;
import dev.jsinco.brewery.bukkit.recipe.DefaultRecipeReader;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.bukkit.structure.BarrelBlockDataMatcher;
import dev.jsinco.brewery.bukkit.structure.StructureReader;
import dev.jsinco.brewery.bukkit.structure.StructureRegistry;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.database.DatabaseDriver;
import dev.jsinco.brewery.effect.DrunkManager;
import dev.jsinco.brewery.effect.text.DrunkTextRegistry;
import dev.jsinco.brewery.recipes.RecipeReader;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;
import dev.jsinco.brewery.util.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.stream.Stream;

public class TheBrewingProject extends JavaPlugin {

    @Getter
    private static TheBrewingProject instance;
    @Getter
    private StructureRegistry structureRegistry;
    @Getter
    private PlacedStructureRegistry placedStructureRegistry;
    @Getter
    private RecipeRegistry<RecipeResult, ItemStack, PotionMeta> recipeRegistry;
    @Getter
    private BreweryRegistry breweryRegistry;
    @Getter
    private Database database;
    @Getter
    private DrunkTextRegistry drunkTextRegistry;
    @Getter
    private DrunkManager drunkManager;

    @Override
    public void onLoad() {
        instance = this;
        this.structureRegistry = new StructureRegistry();
        this.placedStructureRegistry = new PlacedStructureRegistry();
        this.breweryRegistry = new BreweryRegistry();
        loadStructures();
        this.recipeRegistry = new RecipeRegistry<>();
        this.drunkTextRegistry = new DrunkTextRegistry();
    }

    private void loadStructures() {
        File structureRoot = new File(getDataFolder(), "structures");
        if (!structureRoot.exists() && !structureRoot.mkdirs()) {
            throw new RuntimeException("Could not create structure root: " + structureRoot);
        }
        Stream.of("small_barrel", "large_barrel", "bamboo_distillery")
                .map(string -> "structures/" + string)
                .flatMap(name -> Stream.of(name + ".schem", name + ".json"))
                .forEach(this::saveResourceIfNotExists);
        Stream.of(structureRoot.listFiles())
                .filter(file -> file.getName().endsWith(".json"))
                .map(File::toPath)
                .map(path -> {
                    try {
                        return StructureReader.fromJson(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(structure -> {
                    if (structure.getMetaOrDefault(StructureMeta.USE_BARREL_SUBSTITUTION, false)) {
                        structureRegistry.addStructure(structure, BarrelBlockDataMatcher.INSTANCE, BarrelType.PLACEABLE_TYPES);
                    } else {
                        structureRegistry.addStructure(structure);
                    }
                });
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
        this.drunkManager = new DrunkManager(200);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this.structureRegistry, placedStructureRegistry, this.database, this.breweryRegistry), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this.placedStructureRegistry, this.breweryRegistry, this.database, this.drunkManager, this.drunkTextRegistry), this);
        Bukkit.getPluginManager().registerEvents(new InventoryEventListener(breweryRegistry, database), this);
        WorldEventListener worldEventListener = new WorldEventListener(this.database, this.placedStructureRegistry, this.breweryRegistry);
        worldEventListener.init();
        Bukkit.getPluginManager().registerEvents(worldEventListener, this);
        Bukkit.getScheduler().runTaskTimer(this, this::updateStructures, 0, 1);
        Bukkit.getScheduler().runTaskTimer(this, this::otherTicking, 0, 1);
        RecipeReader<RecipeResult, ItemStack> recipeReader = new RecipeReader<>(this.getDataFolder(), new BukkitRecipeResultReader(), BukkitIngredientManager.INSTANCE);

        this.recipeRegistry.registerRecipes(recipeReader.readRecipes());
        this.recipeRegistry.registerDefaultRecipes(DefaultRecipeReader.readDefaultRecipes(this.getDataFolder()));
        getCommand("test").setExecutor(new TestCommand());
        try (InputStream inputStream = Util.class.getResourceAsStream("/drunk_text.json")) {
            drunkTextRegistry.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveResources() {
        this.saveResourceIfNotExists("recipes.yml");
        this.saveResourceIfNotExists("ingredients.yml");
        this.saveResourceIfNotExists("config.yml");
    }

    private void saveResourceIfNotExists(String resource) {
        if (new File(getDataFolder(), resource).exists()) {
            return;
        }
        super.saveResource(resource, false);
    }

    private void updateStructures() {
        breweryRegistry.getActiveCauldrons().forEach(BukkitCauldron::tick);
        breweryRegistry.getOpened(StructureType.BARREL).forEach(Barrel::tick);
        breweryRegistry.getOpened(StructureType.DISTILLERY).forEach(Distillery::tick);
    }

    private void otherTicking() {
        drunkManager.tick(DrunkEventAction::doDrunkEvent);
    }

    public void registerPluginIngredients() {
        PluginIngredient.registerPluginIngredient("Oraxen", OraxenPluginIngredient::new);
    }
}