package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.Distillery;
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
    public void onBrew(BrewEvent event) {
        BreweryLocation location = BukkitAdapter.toBreweryLocation(event.getBlock());
        Optional<BukkitDistillery> distillery = registry.getDistillery(location);
        distillery.ifPresent(ignored -> event.setCancelled(true));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHopperInventorySearch(HopperInventorySearchEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory)) {
            return;
        }
        BreweryLocation location = BukkitAdapter.toBreweryLocation(event.getSearchBlock());
        registry.getDistillery(location).ifPresent(distillery -> ListenerUtil.updateDistillery(distillery, registry, database));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory brewerInventory)) {
            return;
        }
        BrewingStand brewingStand = brewerInventory.getHolder();
        if (brewingStand == null) {
            return;
        }
        BreweryLocation location = BukkitAdapter.toBreweryLocation(brewingStand.getBlock());
        registry.getDistillery(location).ifPresent(distillery -> {
            ListenerUtil.updateDistillery(distillery, registry, database);
            registry.addOpenDistillery(distillery);
        });

    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory brewerInventory) {
            BrewingStand brewingStand = brewerInventory.getHolder();
            if (brewingStand == null) {
                return;
            }
            BreweryLocation location = BukkitAdapter.toBreweryLocation(brewingStand.getBlock());
            Optional<BukkitDistillery> distilleryOptional = registry.getDistillery(location);
            if (distilleryOptional.isEmpty()) {
                BukkitDistillery distillery = new BukkitDistillery(brewingStand.getBlock());
                if (distillery.getState() == Distillery.State.INVALID) {
                    return;
                }
                try {
                    database.insertValue(BukkitDistilleryDataType.INSTANCE, distillery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                registry.addDistillery(distillery);
                registry.addOpenDistillery(distillery);
            }
        }
    }
}
