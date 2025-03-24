package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.bukkit.breweries.*;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WorldEventListener implements Listener {

    private final Database database;
    private final PlacedStructureRegistry placedStructureRegistry;
    private final BreweryRegistry registry;

    public WorldEventListener(Database database, PlacedStructureRegistry placedStructureRegistry, BreweryRegistry registry) {
        this.database = database;
        this.placedStructureRegistry = placedStructureRegistry;
        this.registry = registry;
    }

    public void init() {
        Bukkit.getServer().getWorlds().forEach(this::loadWorld);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        loadWorld(event.getWorld());
    }

    public void onWorldUnload(WorldUnloadEvent event) {
        placedStructureRegistry.unloadWorld(event.getWorld().getUID());
    }

    private void loadWorld(World world) {
        try {
            List<BukkitBarrel> barrelList = database.retrieveAll(BukkitBarrelDataType.INSTANCE, world.getUID());
            for (BukkitBarrel barrel : barrelList) {
                placedStructureRegistry.registerStructure(barrel.getStructure());
                registry.registerInventory(barrel);
            }
            List<BukkitCauldron> cauldrons = database.retrieveAll(BukkitCauldronDataType.INSTANCE, world.getUID());
            List<BukkitDistillery> distilleries = database.retrieveAll(BukkitDistilleryDataType.INSTANCE, world.getUID());
            distilleries.stream()
                    .map(BukkitDistillery::getStructure)
                    .forEach(placedStructureRegistry::registerStructure);
            distilleries.forEach(registry::registerInventory);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
