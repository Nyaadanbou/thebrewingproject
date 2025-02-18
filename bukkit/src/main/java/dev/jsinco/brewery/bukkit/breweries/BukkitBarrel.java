package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.Tickable;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.brew.BukkitBarrelBrewDataType;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class BukkitBarrel implements Barrel, Tickable, InventoryHolder {
    private final PlacedBreweryStructure structure;
    @Getter
    private final @NotNull Inventory inventory;
    @Getter
    private final int size;
    @Getter
    private final BarrelType type;
    @Getter
    private final Location signLocation;
    private Brew<ItemStack>[] brews;

    public BukkitBarrel(Location signLocation, PlacedBreweryStructure structure, int size, BarrelType type) {
        this.structure = structure;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, "Barrel");
        this.type = type;
        this.brews = new Brew[size];
        this.signLocation = signLocation;
    }

    private static @Nullable Location findSignLocation(PlacedBreweryStructure structure) {
        for (BreweryLocation breweryLocation : structure.positions()) {
            Location location = BukkitAdapter.toLocation(breweryLocation);
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

    @Override
    public PlacedBreweryStructure getStructure() {
        return structure;
    }

    private void populateInventory() {
        for (int i = 0; i < brews.length; i++) {
            if (brews[i] == null) {
                continue;
            }
            ItemStack brewItem = BrewAdapter.toItem(brews[i]);
            this.inventory.setItem(i, brewItem);
        }
    }

    private void updateInventory() {
        ItemStack[] inventoryContents = getInventory().getContents();
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack itemStack = inventoryContents[i];
            if (itemStack == null) {
                if (brews[i] != null) {
                    try {
                        TheBrewingProject.getInstance().getDatabase().remove(BukkitBarrelBrewDataType.DATA_TYPE, new Pair<>(brews[i], getContext(i)));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                brews[i] = null;
                continue;
            }
            Optional<Brew<ItemStack>> brewOptional = BrewAdapter.fromItem(itemStack);
            final int iFinal = i;
            brewOptional
                    .map(brew -> {
                        long gameTime = getWorld().getGameTime();
                        Brew<ItemStack> out = brew;
                        if (brew.aging() instanceof Interval interval) {
                            out = brew.withAging(interval.withStop(gameTime));
                        } else if (brew.aging() == null) {
                            out = brew.withAging(new Interval(gameTime, gameTime));
                        }
                        return out.withBarrelType(type);
                    })
                    .ifPresent(brew -> {
                        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                        BrewAdapter.applyMeta(potionMeta, brew);
                        itemStack.setItemMeta(potionMeta);
                        if (Brew.sameValuesForAging(brew, brews[iFinal])) {
                            brews[iFinal] = brew;
                            return;
                        }
                        Database database = TheBrewingProject.getInstance().getDatabase();
                        BukkitBarrelBrewDataType.BarrelContext context = getContext(iFinal);
                        try {
                            if (brews[iFinal] == null) {
                                database.insertValue(BukkitBarrelBrewDataType.DATA_TYPE, new Pair<>(brew, context));
                            } else {
                                database.updateValue(BukkitBarrelBrewDataType.DATA_TYPE, new Pair<>(brew, context));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        brews[iFinal] = brew;
                    });
        }
    }

    private BukkitBarrelBrewDataType.BarrelContext getContext(int inventoryPos) {
        return new BukkitBarrelBrewDataType.BarrelContext(signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ(), inventoryPos, signLocation.getWorld().getUID());
    }

    World getWorld() {
        return signLocation.getWorld();
    }

    public void setBrews(List<Pair<Brew<ItemStack>, Integer>> brews) {
        this.brews = new Brew[9];
        for (Pair<Brew<ItemStack>, Integer> brew : brews) {
            this.brews[brew.second()] = brew.first();
        }
    }

    public List<Pair<Brew<ItemStack>, Integer>> getBrews() {
        List<Pair<Brew<ItemStack>, Integer>> brewList = new ArrayList<>();
        for (int i = 0; i < brews.length; i++) {
            if (brews[i] == null) {
                continue;
            }
            brewList.add(new Pair<>(brews[i], i));
        }
        return brewList;
    }
}
