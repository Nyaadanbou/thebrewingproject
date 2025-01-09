package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BreweryRegistry;
import dev.jsinco.brewery.breweries.Cauldron;
import dev.jsinco.brewery.breweries.Destroyable;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerEventListener implements Listener {

    private final PlacedStructureRegistry placedStructureRegistry;
    private final BreweryRegistry breweryRegistry;

    public PlayerEventListener(PlacedStructureRegistry placedStructureRegistry, BreweryRegistry breweryRegistry) {
        this.placedStructureRegistry = placedStructureRegistry;
        this.breweryRegistry = breweryRegistry;
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
        breweryRegistry.registerOpenedBarrel(playerInteractEvent.getPlayer().getUniqueId(), barrel);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID playerUuid = player.getUniqueId();
        breweryRegistry.getOpenedBarrel(playerUuid).ifPresent(barrel -> {
            barrel.close(player);
            breweryRegistry.removeOpenedBarrel(playerUuid);
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.useItemInHand() == Event.Result.DENY || !event.hasItem()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || !Tag.CAULDRONS.isTagged(block.getType())) {
            return;
        }
        if (!Cauldron.isValidStructure(block)) {
            breweryRegistry.getActiveCauldron(block)
                    .ifPresent(breweryRegistry::removeActiveCauldron);
            return;
        }
        Optional<Cauldron> cauldronOptional = breweryRegistry.getActiveCauldron(block);
        ItemStack itemStack = event.getItem();
        if (isIngredient(itemStack)) {
            Cauldron cauldron = cauldronOptional
                    .orElseGet(() -> new Cauldron(block));
            cauldron.addIngredient(itemStack, event.getPlayer());
            return;
        }
        if (itemStack.getType() == Material.GLASS_BOTTLE) {
            cauldronOptional
                    .flatMap(Cauldron::getBrew)
                    .map(Brew::toItem)
                    .ifPresent(brewItemStack -> {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), brewItemStack);
                        Levelled cauldron = (Levelled) block.getBlockData();
                        if (cauldron.getLevel() == 1) {
                            breweryRegistry.removeActiveCauldron(cauldronOptional.get());
                        }
                        cauldron.setLevel(cauldron.getLevel() - 1);
                        block.setBlockData(cauldron);
                    });
        }
        cauldronOptional.ifPresent(ignored -> {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        });
    }

    private boolean isIngredient(ItemStack itemStack) {
        if (1 == itemStack.getMaxStackSize()) {
            // Probably equipment
            return false;
        }
        Material type = itemStack.getType();
        if (type == Material.BUCKET) {
            return false;
        }
        if (type == Material.POTION) {
            return false;
        }
        return type != Material.GLASS_BOTTLE;
    }
}
