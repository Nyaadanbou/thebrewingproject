package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.*;
import dev.jsinco.brewery.bukkit.breweries.BukkitBarrel;
import dev.jsinco.brewery.bukkit.breweries.BukkitBarrelDataType;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldron;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldronDataType;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
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
    private final PlacedStructureRegistry<PlacedBreweryStructure> placedStructureRegistry;

    public WorldEventListener(Database database, PlacedStructureRegistry<PlacedBreweryStructure> placedStructureRegistry) {
        this.database = database;
        this.placedStructureRegistry = placedStructureRegistry;
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
                Location signLocation = barrel.getSignLocation();
                placedStructureRegistry.registerPosition(BukkitAdapter.toBreweryLocation(signLocation), barrel);
                placedStructureRegistry.registerStructure(barrel.getStructure());
            }
            database.retrieveAll(BukkitCauldronDataType.INSTANCE, world.getUID());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
