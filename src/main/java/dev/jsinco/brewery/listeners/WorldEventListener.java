package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.*;
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
    private final PlacedStructureRegistry placedStructureRegistry;
    private final BreweryRegistry breweryRegistry;

    public WorldEventListener(Database database, PlacedStructureRegistry placedStructureRegistry, BreweryRegistry breweryRegistry) {
        this.database = database;
        this.placedStructureRegistry = placedStructureRegistry;
        this.breweryRegistry = breweryRegistry;
    }

    public void init() {
        Bukkit.getServer().getWorlds().forEach(this::loadWorld);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        loadWorld(event.getWorld());
    }

    public void onWorldUnload(WorldUnloadEvent event) {
        placedStructureRegistry.unloadWorld(event.getWorld());
    }

    private void loadWorld(World world) {
        try {
            List<Barrel> barrelList = database.retrieveAll(BarrelDataType.DATA_TYPE, world);
            for (Barrel barrel : barrelList) {
                Location signLocation = barrel.getSignLocation();
                placedStructureRegistry.registerPosition(signLocation, barrel);
                placedStructureRegistry.registerStructure(barrel.getStructure().get());
            }
            database.retrieveAll(CauldronDataType.DATA_TYPE, world);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
