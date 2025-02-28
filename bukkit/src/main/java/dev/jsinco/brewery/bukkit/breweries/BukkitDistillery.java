package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.listeners.ListenerUtil;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BukkitDistillery implements Distillery {

    private static final int DISTILL_TIME = 400;
    private final Block block;
    @Getter
    private long startTime;

    public BukkitDistillery(Block block) {
        this.block = block;
        this.startTime = block.getWorld().getGameTime();
    }

    public BukkitDistillery(Block block, int startTime) {
        this.block = block;
        this.startTime = startTime;
    }

    @Override
    public BreweryLocation getLocation() {
        return BukkitAdapter.toBreweryLocation(block);
    }

    public State getState() {
        if (block.getState() instanceof BrewingStand brewingStand) {
            BrewerInventory inventory = brewingStand.getInventory();
            State inventoryState = getInventoryState(inventory);
            if (inventoryState != State.INVALID && brewingStand.getFuelLevel() <= 0) {
                return State.PAUSED;
            }
            return inventoryState;
        }
        return State.INVALID;
    }

    private int getDistillRunTime(BrewerInventory inventory) {
        List<Brew<ItemStack>> brews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            Optional<Brew<ItemStack>> brew = BrewAdapter.fromItem(item);
            brew.ifPresent(brews::add);
        }
        int maxDistillRunTime = -1;
        for (Brew<ItemStack> brew : brews) {
            Optional<Recipe<RecipeResult, ItemStack>> recipe = brew.closestRecipe(TheBrewingProject.getInstance().getRecipeRegistry());
            if (recipe.isPresent()) {
                if (maxDistillRunTime < recipe.get().getDistillTime()) {
                    maxDistillRunTime = recipe.get().getDistillTime();
                }
            }
        }
        return maxDistillRunTime == -1 ? DISTILL_TIME : maxDistillRunTime;
    }

    public void applyToBlock(State state) {
        if (state == State.INVALID || !(block.getState() instanceof BrewingStand brewingStand)) {
            return;
        }
        int distillRunTime = getDistillRunTime(brewingStand.getSnapshotInventory());
        int totalBrewTime = (int) (block.getWorld().getGameTime() - getStartTime());
        int runsCompleted = Math.floorDiv(totalBrewTime, distillRunTime);
        if (runsCompleted > 0) {
            modifyBrews(runsCompleted, brewingStand.getSnapshotInventory());
            startTime = brewingStand.getWorld().getGameTime();
            try {
                TheBrewingProject.getInstance().getDatabase().updateValue(BukkitDistilleryDataType.INSTANCE, this);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (state == State.PAUSED) {
            brewingStand.setBrewingTime(Short.MAX_VALUE);
            brewingStand.update();
            return;
        }
        int distillCompletion = totalBrewTime % distillRunTime;
        brewingStand.setBrewingTime(DISTILL_TIME - distillCompletion * DISTILL_TIME / distillRunTime);
        brewingStand.update();
    }

    private void modifyBrews(int runsCompleted, BrewerInventory inventory) {
        List<Brew<ItemStack>> brews = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                brews.add(null);
                continue;
            }
            Optional<Brew<ItemStack>> brew = BrewAdapter.fromItem(item);
            brews.add(brew.orElse(null));
        }
        for (int i = 0; i < 3; i++) {
            Brew<ItemStack> brew = brews.get(i);
            if (brew == null) {
                continue;
            }
            Brew<ItemStack> newBrew = brew.withDistillAmount(runsCompleted + brew.distillRuns());
            ItemStack item = inventory.getItem(i);
            if(!(item.getItemMeta() instanceof PotionMeta potionMeta)) {
                continue;
            }
            BrewAdapter.applyMeta(potionMeta, newBrew);
            item.setItemMeta(potionMeta);
            inventory.setItem(i, item);
        }
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
        if (inventory.getIngredient() == null || inventory.getIngredient().getType() != Material.GLOWSTONE_DUST) {
            return State.PAUSED;
        }
        return invalidBrew ? State.PAUSED : State.RUNNING;
    }

    public static void distilleryUpdateTask(BreweryRegistry registry) {
        List<BukkitDistillery> toClose = new ArrayList<>();
        List<BukkitDistillery> invalid = new ArrayList<>();
        for (BukkitDistillery distillery : registry.getOpenedDistilleries()) {
            State state = distillery.getState();
            if (state == State.INVALID) {
                toClose.add(distillery);
                invalid.add(distillery);
                continue;
            }
            distillery.applyToBlock(state);
            if (BukkitAdapter.toLocation(distillery.getLocation()).getBlock().getState() instanceof BrewingStand brewingStand && brewingStand.getInventory().getViewers().isEmpty()) {
                toClose.add(distillery);
            }
        }
        toClose.forEach(registry::removeOpenedDistillery);
        invalid.forEach(distillery -> ListenerUtil.updateDistillery(distillery, registry, TheBrewingProject.getInstance().getDatabase()));
    }
}
