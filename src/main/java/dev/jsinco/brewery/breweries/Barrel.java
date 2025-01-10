package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.util.moment.Interval;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Barrel implements Tickable, InventoryHolder, Destroyable {

    private final UUID objectId;
    private final PlacedBreweryStructure structure;
    @Getter
    private final @NotNull Inventory inventory;
    private final int size;
    private final BarrelType barrelType;
    private final Location signLocation;
    private Brew[] brews;

    public Barrel(PlacedBreweryStructure structure, int size, BarrelType barrelType) {
        this.objectId = UUID.randomUUID();
        this.structure = structure;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, "Barrel");
        this.barrelType = barrelType;
        this.signLocation = findSignLocation(structure);
        this.brews = new Brew[size];
    }

    public Barrel(UUID objectId, PlacedBreweryStructure structure, Inventory inventory, int size, BarrelType barrelType) {
        this.objectId = objectId;
        this.structure = structure;
        this.inventory = inventory;
        ;
        this.size = size;
        this.barrelType = barrelType;
        this.signLocation = findSignLocation(structure);
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
        if (inventory.getViewers().isEmpty()) {
            populateInventory();
        }
        float randPitch = (float) (Math.random() * 0.1);
        if (signLocation != null) {
            signLocation.getWorld().playSound(signLocation, Sound.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        }
        player.openInventory(inventory);
    }

    private void close() {
        float randPitch = (float) (Math.random() * 0.1);
        if (signLocation != null) {
            signLocation.getWorld().playSound(signLocation, Sound.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5f, 0.8f + randPitch);
        }
        this.inventory.clear();
    }

    @Override
    public void tick() {
        updateInventory();
        if (inventory.getViewers().isEmpty()) {
            close();
            TheBrewingProject.getInstance().getBreweryRegistry().removeOpenedBarrel(this);
        }
    }

    @Override
    public void destroy() {
        // TODO: What should be done when this barrel is destroyed? Probably drop all the brews, right?
    }

    private void populateInventory() {
        for (int i = 0; i < brews.length; i++) {
            if (brews[i] == null) {
                continue;
            }
            ItemStack brewItem = brews[i].toItem();
            this.inventory.setItem(i, brewItem);
        }
    }

    private void updateInventory() {
        ItemStack[] inventoryContents = getInventory().getContents();
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack itemStack = inventoryContents[i];
            if (itemStack == null) {
                brews[i] = null;
                continue;
            }
            Optional<Brew> brewOptional = Brew.fromItem(itemStack);
            final int iFinal = i;
            brewOptional
                    .map(brew -> {
                        long gameTime = getWorld().getGameTime();
                        Brew out = brew;
                        if (brew.aging() instanceof Interval interval) {
                            out = brew.withAging(interval.withStop(gameTime));
                        } else if (brew.aging() == null) {
                            out = brew.withAging(new Interval(gameTime, gameTime));
                        }
                        return out.withBarrelType(barrelType);
                    })
                    .ifPresent(brew -> {
                        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                        brew.applyMeta(potionMeta);
                        itemStack.setItemMeta(potionMeta);
                        brews[iFinal] = brew;
                    });
        }
    }

    private World getWorld() {
        return signLocation.getWorld();
    }
}
