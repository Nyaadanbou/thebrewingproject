package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.*;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Optional;

public class PlayerEventListener implements Listener {

    private final PlacedStructureRegistry placedStructureRegistry;
    private final BreweryRegistry breweryRegistry;
    private final Database database;

    public PlayerEventListener(PlacedStructureRegistry placedStructureRegistry, BreweryRegistry breweryRegistry, Database database) {
        this.placedStructureRegistry = placedStructureRegistry;
        this.breweryRegistry = breweryRegistry;
        this.database = database;
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
        BehaviorHolder holder = possiblePlacedBreweryStructure.get().getHolder();
        if (!(holder instanceof Barrel barrel)) {
            return;
        }
        barrel.open(playerInteractEvent.getPlayer());
        breweryRegistry.registerOpenedBarrel(barrel);
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
                    .ifPresent(cauldron -> ListenerUtil.removeCauldron(cauldron, breweryRegistry, database));
            return;
        }
        Optional<Cauldron> cauldronOptional = breweryRegistry.getActiveCauldron(block);
        ItemStack itemStack = event.getItem();
        if (isIngredient(itemStack)) {
            Cauldron cauldron = cauldronOptional
                    .orElseGet(() -> this.initCauldron(block));
            cauldron.addIngredient(itemStack, event.getPlayer());
            try {
                database.updateValue(CauldronDataType.DATA_TYPE, cauldron);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                            ListenerUtil.removeCauldron(cauldronOptional.get(), breweryRegistry, database);
                            block.setType(Material.CAULDRON);
                            return;
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

    private Cauldron initCauldron(Block block) {
        Cauldron newCauldron = new Cauldron(block);
        try {
            database.insertValue(CauldronDataType.DATA_TYPE, newCauldron);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newCauldron;
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
