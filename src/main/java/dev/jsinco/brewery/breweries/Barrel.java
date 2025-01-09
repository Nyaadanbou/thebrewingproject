package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Class for traditional barrels which use BoundingBoxes to determine the area of the barrel.
 */
@Getter
public class Barrel implements Tickable, InventoryHolder, Destroyable {

    private final UUID objectId;
    private final PlacedBreweryStructure structure;
    private final Inventory inventory;
    private final int size;
    private final BarrelType barrelType;
    private final Location signLocation;
    private final Set<UUID> currentlyOpenedBy = new HashSet<>();
    private long lastUpdated;

    public Barrel(PlacedBreweryStructure structure, int size, BarrelType barrelType) {
        this.objectId = UUID.randomUUID();
        this.structure = structure;
        this.inventory = Bukkit.createInventory(this, size, "Barrel");
        this.size = size;
        this.barrelType = barrelType;
        this.signLocation = findSignLocation(structure);
        this.lastUpdated = structure.getPositions().getLast().getWorld().getGameTime();
    }

    public Barrel(UUID objectId, PlacedBreweryStructure structure, Inventory inventory, int size, BarrelType barrelType, long lastOpened) {
        this.objectId = objectId;
        this.structure = structure;
        this.inventory = inventory;
        this.size = size;
        this.barrelType = barrelType;
        this.signLocation = findSignLocation(structure);
        this.lastUpdated = lastOpened;
    }

    private static @Nullable Location findSignLocation(PlacedBreweryStructure structure) {
        for (Location location : structure.getPositions()) {
            if (Tag.WALL_SIGNS.isTagged(location.getBlock().getType())) {
                return location;
            }
        }
        return null;
    }


    public void open(Player player) {
        if (lastUpdated < structure.getPositions().getFirst().getWorld().getGameTime() - 1) {
            tick();
        }
        float randPitch = (float) (Math.random() * 0.1);
        if (signLocation != null) {
            signLocation.getWorld().playSound(signLocation, Sound.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        }
        player.openInventory(inventory);
    }

    public void close(Player player) {
        float randPitch = (float) (Math.random() * 0.1);
        if (signLocation != null) {
            signLocation.getWorld().playSound(signLocation, Sound.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        }
    }

    @Override
    public void tick() {
        this.lastUpdated = structure.getPositions().getFirst().getWorld().getGameTime();
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(String.valueOf(lastUpdated));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(0, itemStack);
    }

    @Override
    public void destroy() {
        // TODO: What should be done when this barrel is destroyed? Probably drop all the brews, right?
    }
}
