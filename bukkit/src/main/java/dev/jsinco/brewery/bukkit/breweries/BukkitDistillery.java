package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BukkitDistillery implements Distillery {

    private final Block block;
    @Getter
    private final long startTime;

    public BukkitDistillery(Block block) {
        this.block = block;
        this.startTime = block.getWorld().getGameTime();
    }

    @Override
    public BreweryLocation getLocation() {
        return BukkitAdapter.toBreweryLocation(block);
    }

    public State getState() {
        if (block.getState() instanceof BrewingStand brewingStand) {
            BrewerInventory inventory = brewingStand.getInventory();
            return getInventoryState(inventory);
        }
        return State.INVALID;
    }

    public void applyToBlock(State state) {

    }

    public static State getInventoryState(BrewerInventory inventory) {
        boolean invalidBrew = false;
        boolean hasBrew = false;
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            Optional<Brew<ItemStack>> brewOptional = BrewAdapter.fromItem(item);
            if (brewOptional.isEmpty()) {
                invalidBrew = true;
            } else {
                hasBrew = true;
            }
        }
        if (!hasBrew) {
            return State.INVALID;
        }
        if (inventory.getFuel() == null && inventory.getFuel().getType() != Material.GLOWSTONE_DUST) {
            return State.PAUSED;
        }
        return invalidBrew ? State.PAUSED : State.RUNNING;
    }
}
