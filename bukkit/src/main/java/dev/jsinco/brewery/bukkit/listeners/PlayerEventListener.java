package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.InventoryAccessible;
import dev.jsinco.brewery.breweries.StructureHolder;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldron;
import dev.jsinco.brewery.bukkit.breweries.BukkitCauldronDataType;
import dev.jsinco.brewery.bukkit.recipe.RecipeEffects;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.effect.DrunkManager;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.effect.text.DrunkTextRegistry;
import dev.jsinco.brewery.effect.text.DrunkTextTransformer;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class PlayerEventListener implements Listener {

    private final PlacedStructureRegistry placedStructureRegistry;
    private final BreweryRegistry breweryRegistry;
    private final Database database;
    private final DrunkManager drunkManager;
    private final DrunkTextRegistry drunkTextRegistry;
    private final RecipeRegistry<ItemStack, PotionMeta> recipeRegistry;

    public PlayerEventListener(PlacedStructureRegistry placedStructureRegistry, BreweryRegistry breweryRegistry, Database database, DrunkManager drunkManager, DrunkTextRegistry drunkTextRegistry, RecipeRegistry<ItemStack, PotionMeta> recipeRegistry) {
        this.placedStructureRegistry = placedStructureRegistry;
        this.breweryRegistry = breweryRegistry;
        this.database = database;
        this.drunkManager = drunkManager;
        this.drunkTextRegistry = drunkTextRegistry;
        this.recipeRegistry = recipeRegistry;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPLayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getPlayer().isSneaking()) {
            return;
        }
        Optional<StructureHolder> possibleStructureHolder = placedStructureRegistry.getHolder(BukkitAdapter.toBreweryLocation(playerInteractEvent.getClickedBlock().getLocation()));
        if (possibleStructureHolder.isEmpty()) {
            return;
        }
        if (possibleStructureHolder.get() instanceof InventoryAccessible inventoryAccessible) {
            inventoryAccessible.open(BukkitAdapter.toBreweryLocation(playerInteractEvent.getClickedBlock()), playerInteractEvent.getPlayer().getUniqueId());
            breweryRegistry.registerOpened(inventoryAccessible);
        }
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
        if (!BukkitCauldron.isValidStructure(block)) {
            breweryRegistry.getActiveCauldron(BukkitAdapter.toBreweryLocation(block))
                    .ifPresent(cauldron -> ListenerUtil.removeCauldron(cauldron, breweryRegistry, database));
            return;
        }
        Optional<BukkitCauldron> cauldronOptional = breweryRegistry.getActiveCauldron(BukkitAdapter.toBreweryLocation(block));
        ItemStack itemStack = event.getItem();
        if (isIngredient(itemStack)) {
            BukkitCauldron cauldron = cauldronOptional
                    .orElseGet(() -> this.initCauldron(block));
            cauldron.addIngredient(itemStack, event.getPlayer());
            try {
                database.updateValue(BukkitCauldronDataType.INSTANCE, cauldron);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (itemStack.getType() == Material.GLASS_BOTTLE) {
            cauldronOptional
                    .flatMap(BukkitCauldron::getBrew)
                    .map(BrewAdapter::toItem)
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

    private BukkitCauldron initCauldron(Block block) {
        BukkitCauldron newCauldron = new BukkitCauldron(block);
        try {
            database.insertValue(BukkitCauldronDataType.INSTANCE, newCauldron);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        DrunkState drunkState = drunkManager.getDrunkState(playerUuid);
        if (drunkState == null) {
            return;
        }
        String text = event.getMessage();
        String transformed = DrunkTextTransformer.transform(text, drunkTextRegistry, drunkState.alcohol());
        event.setMessage(transformed);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!(event.getItem().getItemMeta() instanceof PotionMeta potionMeta)) {
            return;
        }
        PersistentDataContainer persistentDataContainer = event.getItem().getItemMeta().getPersistentDataContainer();
        drunkManager.consume(event.getPlayer().getUniqueId(), persistentDataContainer.get(RecipeEffects.ALCOHOL, PersistentDataType.INTEGER));

    }
}
