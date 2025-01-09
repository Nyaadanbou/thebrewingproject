package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.Destroyable;
import dev.jsinco.brewery.breweries.BreweryRegistry;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.UUID;

public class PlayerEventListener implements Listener {

    private final PlacedStructureRegistry placedStructureRegistry;

    public PlayerEventListener(PlacedStructureRegistry placedStructureRegistry) {
        this.placedStructureRegistry = placedStructureRegistry;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPLayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getPlayer().isSneaking()) {
            return;
        }
        Optional<PlacedBreweryStructure> possiblePlacedBreweryStructure = placedStructureRegistry.getStructure(playerInteractEvent.getClickedBlock().getLocation());
        if (possiblePlacedBreweryStructure.isEmpty()) {
            return;
        }
        Destroyable holder = possiblePlacedBreweryStructure.get().getHolder();
        if (!(holder instanceof Barrel barrel)) {
            return;
        }
        barrel.open(playerInteractEvent.getPlayer());
        BreweryRegistry.getOpenedBarrels().put(playerInteractEvent.getPlayer().getUniqueId(), barrel);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID playerUuid = player.getUniqueId();
        Barrel barrel = BreweryRegistry.getOpenedBarrels().get(playerUuid);
        if (barrel == null) {
            return;
        }
        barrel.close(player);
        BreweryRegistry.getOpenedBarrels().remove(playerUuid);
    }
}
