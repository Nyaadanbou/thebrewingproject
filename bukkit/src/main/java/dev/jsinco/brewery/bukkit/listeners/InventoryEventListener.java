package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.InventoryAccessible;
import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.BukkitDistillery;
import dev.jsinco.brewery.bukkit.breweries.BukkitDistilleryDataType;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.BrewerInventory;

import java.sql.SQLException;
import java.util.Optional;

public class InventoryEventListener implements Listener {

    private final BreweryRegistry registry;
    private final Database database;

    public InventoryEventListener(BreweryRegistry registry, Database database) {
        this.registry = registry;
        this.database = database;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAccessible inventoryAccessible = registry.getFromInventory(event.getInventory());
    }
}
