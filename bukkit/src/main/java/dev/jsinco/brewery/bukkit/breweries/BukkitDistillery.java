package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.brew.BukkitDistilleryBrewDataType;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.util.Logging;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

public class BukkitDistillery implements Distillery<BukkitDistillery, ItemStack, Inventory> {

    @Getter
    private final PlacedBreweryStructure<BukkitDistillery> structure;
    @Getter
    private long startTime;
    @Getter
    private final DistilleryInventory mixture;
    @Getter
    private final DistilleryInventory distillate;
    private boolean dirty = true;
    private final Set<BreweryLocation> mixtureContainerLocations = new HashSet<>();
    private final Set<BreweryLocation> distillateContainerLocations = new HashSet<>();


    public BukkitDistillery(@NotNull PlacedBreweryStructure<BukkitDistillery> structure) {
        this(structure, structure.getWorldOrigin().getWorld().getGameTime());
    }

    public BukkitDistillery(@NotNull PlacedBreweryStructure<BukkitDistillery> structure, long startTime) {
        this.structure = structure;
        this.startTime = startTime;
        this.mixture = new DistilleryInventory("Distillery Mixture", structure.getStructure().getMeta(StructureMeta.INVENTORY_SIZE), this);
        this.distillate = new DistilleryInventory("Distillery Distillate", structure.getStructure().getMeta(StructureMeta.INVENTORY_SIZE), this);
    }

    public void open(@NotNull BreweryLocation location, @NotNull UUID playerUuid) {
        checkDirty();
        Player player = Bukkit.getPlayer(playerUuid);
        if (mixtureContainerLocations.contains(location)) {
            mixture.updateInventoryFromBrews();
            TheBrewingProject.getInstance().getBreweryRegistry().registerOpened(this);
            player.openInventory(mixture.getInventory());
            return;
        }
        if (distillateContainerLocations.contains(location)) {
            distillate.updateInventoryFromBrews();
            TheBrewingProject.getInstance().getBreweryRegistry().registerOpened(this);
            player.openInventory(distillate.getInventory());
        }
    }

    @Override
    public boolean inventoryAllows(@NotNull UUID playerUuid, @NotNull ItemStack item) {
        //TODO permissions
        return BrewAdapter.fromItem(item)
                .map(brew -> brew.aging() == null)
                .orElse(false);
    }

    @Override
    public Set<Inventory> getInventories() {
        return Set.of(mixture.getInventory(), distillate.getInventory());
    }

    /**
     * Made to avoid chunk access on startup
     */
    private void checkDirty() {
        if (!dirty) {
            return;
        }
        dirty = false;
        Set<BreweryLocation> potLocations = new HashSet<>();
        Material taggedMaterial = Material.valueOf(structure.getStructure().getMeta(StructureMeta.TAGGED_MATERIAL).toUpperCase(Locale.ROOT));
        for (BreweryLocation location : structure.positions()) {
            Block block = BukkitAdapter.toBlock(location);
            if (taggedMaterial == block.getType()) {
                potLocations.add(location);
            }
        }
        for (BreweryLocation breweryLocation : potLocations) {
            if (potLocations.contains(breweryLocation.add(0, 1, 0)) || potLocations.contains(breweryLocation.add(0, -1, 0))) {
                mixtureContainerLocations.add(breweryLocation);
            } else {
                distillateContainerLocations.add(breweryLocation);
            }
        }
    }

    public void tick() {
        checkDirty();
        boolean hasChanged;
        if (mixture.getInventory().getViewers().isEmpty()) {
            mixture.getInventory().clear(); // Depopulate inventory, save on memory
            hasChanged = false;
        } else {
            hasChanged = mixture.updateBrewsFromInventory();
        }
        if (distillate.getInventory().getViewers().isEmpty()) {
            distillate.getInventory().clear(); // Depopulate inventory, save on memory
        } else {
            distillate.updateBrewsFromInventory();
        }
        if (hasChanged) {
            Logging.log("Inventory has changed, resetting start time");
            resetStartTime();
        }
        if (distillate.getInventory().getViewers().isEmpty() && mixture.getInventory().getViewers().isEmpty()) {
            TheBrewingProject.getInstance().getBreweryRegistry().unregisterOpened(this);
        }
        if (structure.getWorldOrigin().getWorld().getGameTime() - startTime < getStructure().getStructure().getMeta(StructureMeta.PROCESS_TIME) || mixture.isEmpty()) {
            return;
        }
        transferItems(mixture, distillate);
        if (!distillate.getInventory().getViewers().isEmpty()) {
            distillate.updateInventoryFromBrews();
        }
        if (!mixture.getInventory().getViewers().isEmpty()) {
            mixture.updateInventoryFromBrews();
        }
        resetStartTime();
    }

    private void resetStartTime() {
        startTime = Bukkit.getWorld(getStructure().getUnique().worldUuid()).getGameTime();
    }

    private void transferItems(DistilleryInventory inventory1, DistilleryInventory inventory2) {
        Brew<ItemStack>[] brews = inventory1.getBrews();
        int i = 0;
        for (int j = 0; j < inventory2.getBrews().length; j++) {
            if (inventory2.getBrews()[j] != null) {
                continue; // Avoid overriding brews
            }
            while (i < brews.length) {
                if (brews[i] != null) {
                    break;
                }
                i++;
            }
            if (i >= brews.length) {
                return;
            }
            Brew<ItemStack> mixtureBrew = inventory1.getBrews()[i];
            inventory1.store(null, i);
            inventory2.store(mixtureBrew.withDistillAmount(mixtureBrew.distillRuns() + 1), j);
        }
    }

    @Override
    public void destroy() {
        //TODO
    }

    public static class DistilleryInventory implements InventoryHolder {

        private final Inventory inventory;
        @Getter
        private final Brew<ItemStack>[] brews;
        private final BukkitDistillery owner;

        public DistilleryInventory(String title, int size, BukkitDistillery owner) {
            this.inventory = Bukkit.createInventory(this, size, title);
            this.brews = new Brew[size];
            this.owner = owner;
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        /**
         * Set an item in the inventory without changing the database
         *
         * @param brew
         * @param position
         */
        public void set(@Nullable Brew<ItemStack> brew, int position) {
            brews[position] = brew;
        }

        /**
         * Set an item in the inventory and store the changes in the database
         *
         * @param brew
         * @param position
         */
        public void store(@Nullable Brew<ItemStack> brew, int position) {
            if (Objects.equals(brews[position], brew)) {
                return;
            }
            try {
                Brew<ItemStack> previous = brews[position];
                set(brew, position);
                BreweryLocation unique = owner.getStructure().getUnique();
                BukkitDistilleryBrewDataType.DistilleryContext context = new BukkitDistilleryBrewDataType.DistilleryContext(unique.x(), unique.y(), unique.z(), unique.worldUuid(), position, this == owner.getDistillate());
                Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext> data = new Pair<>(brew, context);
                if (previous == null) {
                    TheBrewingProject.getInstance().getDatabase().insertValue(BukkitDistilleryBrewDataType.INSTANCE, data);
                    return;
                }
                if (brew == null) {
                    TheBrewingProject.getInstance().getDatabase().remove(BukkitDistilleryBrewDataType.INSTANCE, data);
                    return;
                }
                TheBrewingProject.getInstance().getDatabase().updateValue(BukkitDistilleryBrewDataType.INSTANCE, data);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        public void updateInventoryFromBrews() {
            for (int i = 0; i < brews.length; i++) {
                Brew<ItemStack> brew = brews[i];
                if (brew == null) {
                    inventory.setItem(i, null);
                    continue;
                }
                inventory.setItem(i, BrewAdapter.toItem(brew));
            }
        }

        public boolean updateBrewsFromInventory() {
            boolean hasUpdated = false;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                Brew<ItemStack> brew;
                if (itemStack == null) {
                    brew = null;
                } else {
                    brew = BrewAdapter.fromItem(itemStack).orElse(null);
                }
                if (!Objects.equals(brew, brews[i])) {
                    hasUpdated = true;
                }
                store(brew, i);
            }
            return hasUpdated;
        }

        public boolean isEmpty() {
            for (Brew<ItemStack> brew : brews) {
                if (brew != null) {
                    return false;
                }
            }
            return true;
        }
    }
}
