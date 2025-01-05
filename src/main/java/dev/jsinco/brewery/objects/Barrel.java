package dev.jsinco.brewery.objects;

import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.util.Logging;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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

    public Barrel(PlacedBreweryStructure boundingBox, int size, BarrelType barrelType) {
        this.objectId = UUID.randomUUID();
        this.structure = boundingBox;
        this.inventory = Bukkit.createInventory(this, size, "Barrel");
        this.size = size;
        this.barrelType = barrelType;
        Logging.log(String.format("Created a new barrel: %s %s", size, barrelType));
    }

    public Barrel(UUID objectId, PlacedBreweryStructure structure, Inventory inventory, int size, BarrelType barrelType) {
        this.objectId = objectId;
        this.structure = structure;
        this.inventory = inventory;
        this.size = size;
        this.barrelType = barrelType;
    }


    public void open(Player player, Location clickedLocation) {
        float randPitch = (float) (Math.random() * 0.1);
        clickedLocation.getWorld().playSound(clickedLocation, Sound.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        player.openInventory(inventory);
    }

    public void close(Player player, Location clickedLocation) {
        float randPitch = (float) (Math.random() * 0.1);
        clickedLocation.getWorld().playSound(clickedLocation, Sound.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        player.closeInventory();
    }

    @Override
    public void tick() {
        for (ItemStack item : inventory.getContents()) {
            // code for aging potions once implemented
        }
    }

    @Override
    public void destroy() {
        // TODO: What should be done when this barrel is destroyed? Probably drop all the brews, right?
        Logging.log("Destroyed a barrel");
    }
}
