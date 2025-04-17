package dev.jsinco.brewery.bukkit;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.breweries.Tickable;
import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.BukkitBarrel;
import dev.jsinco.brewery.bukkit.breweries.BukkitDistillery;
import dev.jsinco.brewery.bukkit.command.BreweryCommand;
import dev.jsinco.brewery.bukkit.effect.SqlDrunkStateDataType;
import dev.jsinco.brewery.bukkit.effect.event.CustomDrunkEventReader;
import dev.jsinco.brewery.bukkit.effect.event.DrunkEventExecutor;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.listeners.BlockEventListener;
import dev.jsinco.brewery.bukkit.listeners.InventoryEventListener;
import dev.jsinco.brewery.bukkit.listeners.PlayerEventListener;
import dev.jsinco.brewery.bukkit.listeners.WorldEventListener;
import dev.jsinco.brewery.bukkit.recipe.BukkitRecipeResultReader;
import dev.jsinco.brewery.bukkit.recipe.DefaultRecipeReader;
import dev.jsinco.brewery.bukkit.structure.BarrelBlockDataMatcher;
import dev.jsinco.brewery.bukkit.structure.StructureReader;
import dev.jsinco.brewery.bukkit.structure.StructureRegistry;
import dev.jsinco.brewery.bukkit.util.BreweryTimeDataType;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.database.PersistenceException;
import dev.jsinco.brewery.database.sql.Database;
import dev.jsinco.brewery.database.sql.DatabaseDriver;
import dev.jsinco.brewery.effect.DrunksManager;
import dev.jsinco.brewery.effect.event.CustomEventRegistry;
import dev.jsinco.brewery.effect.text.DrunkTextRegistry;
import dev.jsinco.brewery.recipes.RecipeReader;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TheBrewingProject extends JavaPlugin {

    @Getter
    private static TheBrewingProject instance;
    @Getter
    private StructureRegistry structureRegistry;
    @Getter
    private PlacedStructureRegistry placedStructureRegistry;
    @Getter
    private RecipeRegistry<ItemStack> recipeRegistry;
    @Getter
    private BreweryRegistry breweryRegistry;
    @Getter
    private Database database;
    @Getter
    private DrunkTextRegistry drunkTextRegistry;
    @Getter
    private DrunksManager<Connection> drunksManager;
    @Getter
    private CustomEventRegistry customDrunkEventRegistry;
    private WorldEventListener worldEventListener;
    @Getter
    private DrunkEventExecutor drunkEventExecutor;
    @Getter
    private long time;

    @Override
    public void onLoad() {
        instance = this;
        Config.reload(this.getDataFolder());
        TranslationsConfig.reload(this.getDataFolder());
        this.structureRegistry = new StructureRegistry();
        this.placedStructureRegistry = new PlacedStructureRegistry();
        this.breweryRegistry = new BreweryRegistry();
        loadStructures();
        this.recipeRegistry = new RecipeRegistry<>();
        this.drunkTextRegistry = new DrunkTextRegistry();
        this.customDrunkEventRegistry = new CustomEventRegistry();
        this.drunkEventExecutor = new DrunkEventExecutor();
        CustomDrunkEventReader.read(Config.CUSTOM_EVENTS).forEach(customDrunkEventRegistry::registerCustomEvent);
    }

    public void reload() {
        Config.reload(this.getDataFolder());
        TranslationsConfig.reload(this.getDataFolder());
        this.structureRegistry.clear();
        this.placedStructureRegistry.clear();
        this.breweryRegistry.clear();
        loadStructures();
        this.drunkTextRegistry.clear();
        this.customDrunkEventRegistry.clear();
        this.drunkEventExecutor.clear();
        CustomDrunkEventReader.read(Config.CUSTOM_EVENTS).forEach(customDrunkEventRegistry::registerCustomEvent);
        saveResources();
        this.database = new Database(DatabaseDriver.SQLITE);
        try {
            database.init(this.getDataFolder());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e); // Hard exit if any issues here
        }
        this.drunksManager.reset(Config.ENABLED_RANDOM_EVENTS.stream().map(BreweryKey::parse).collect(Collectors.toSet()));
        worldEventListener.init();
        RecipeReader<ItemStack> recipeReader = new RecipeReader<>(this.getDataFolder(), new BukkitRecipeResultReader(), BukkitIngredientManager.INSTANCE);

        this.recipeRegistry.registerRecipes(recipeReader.readRecipes());
        this.recipeRegistry.registerDefaultRecipes(DefaultRecipeReader.readDefaultRecipes(this.getDataFolder()));
        try (InputStream inputStream = Util.class.getResourceAsStream("/drunk_text.json")) {
            drunkTextRegistry.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            this.time = database.getSingleton(BreweryTimeDataType.INSTANCE);
        } catch (IOException | PersistenceException | SQLException e) {
            throw new RuntimeException(e); // Hard exit if any issues here
        }
        this.drunksManager = new DrunksManager<>(customDrunkEventRegistry, Config.ENABLED_RANDOM_EVENTS.stream().map(BreweryKey::parse).collect(Collectors.toSet()), () -> this.time, database, SqlDrunkStateDataType.INSTANCE);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this.structureRegistry, placedStructureRegistry, this.database, this.breweryRegistry), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(this.placedStructureRegistry, this.breweryRegistry, this.database, this.drunksManager, this.drunkTextRegistry, recipeRegistry, drunkEventExecutor), this);
        Bukkit.getPluginManager().registerEvents(new InventoryEventListener(breweryRegistry, database), this);
        this.worldEventListener = new WorldEventListener(this.database, this.placedStructureRegistry, this.breweryRegistry);
        worldEventListener.init();
        Bukkit.getPluginManager().registerEvents(worldEventListener, this);
        Bukkit.getScheduler().runTaskTimer(this, this::updateStructures, 0, 1);
        Bukkit.getScheduler().runTaskTimer(this, this::otherTicking, 0, 1);
        RecipeReader<ItemStack> recipeReader = new RecipeReader<>(this.getDataFolder(), new BukkitRecipeResultReader(), BukkitIngredientManager.INSTANCE);

        this.recipeRegistry.registerRecipes(recipeReader.readRecipes());
        this.recipeRegistry.registerDefaultRecipes(DefaultRecipeReader.readDefaultRecipes(this.getDataFolder()));
        getCommand("brew").setExecutor(new BreweryCommand());
        try (InputStream inputStream = Util.class.getResourceAsStream("/drunk_text.json")) {
            drunkTextRegistry.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            database.setSingleton(BreweryTimeDataType.INSTANCE, time);
        } catch (PersistenceException e) {
            e.printStackTrace();
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
        breweryRegistry.getActiveSinglePositionStructure().stream()
                .filter(Tickable.class::isInstance)
                .map(Tickable.class::cast)
                .forEach(Tickable::tick);
        breweryRegistry.<BukkitBarrel>getOpened(StructureType.BARREL).forEach(Barrel::tick);
        breweryRegistry.<BukkitDistillery>getOpened(StructureType.DISTILLERY).forEach(Distillery::tick);
    }

    private void otherTicking() {
        drunksManager.tick(drunkEventExecutor::doDrunkEvent);
        try {
            if (++time % 200 == 0) {
                database.setSingleton(BreweryTimeDataType.INSTANCE, time);
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
}