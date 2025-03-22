package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.InventoryAccessible;
import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.database.Database;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryEventListener implements Listener {

    private final BreweryRegistry registry;
    private final Database database;

    public InventoryEventListener(BreweryRegistry registry, Database database) {
        this.registry = registry;
        this.database = database;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAccessible<ItemStack> inventoryAccessible = registry.getFromInventory(event.getClickedInventory());
        if (inventoryAccessible == null) {
            return;
        }
        if (!inventoryAccessible.inventoryAllows(event.getView().getPlayer().getUniqueId(), event.getCursor())) {
            event.setResult(Event.Result.DENY);
        }
    }
}
