package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.listeners.ListenerUtil;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.bukkit.util.BlockUtil;
import dev.jsinco.brewery.bukkit.util.ColorUtil;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class BukkitCauldron implements dev.jsinco.brewery.breweries.Cauldron<ItemStack> {

    private static final Random RANDOM = new Random();

    private final Map<Ingredient<ItemStack>, Integer> ingredients;
    private final Block block;


    private long brewStart;

    // To determine the closest recipe:
    // Every time @tick is run, check all reducedrecipes/recipes list and see if the ingredients match
    // if they do, set the closest recipe to that recipe
    private @Nullable Recipe<RecipeResult, ItemStack> closestRecipe = null;
    // To determine particle effect color:
    // Every time @tick is run and if closest recipe is NOT null, get color from the closest recipe
    // and gradually shift color to it. If closest recipe becomes null, reset this back to AQUA
    private Color particleColor = Color.AQUA;


    public BukkitCauldron(Block block) {
        this.ingredients = new HashMap<>();
        this.block = block;

        TheBrewingProject.getInstance().getBreweryRegistry().addActiveCauldron(this);
    }

    // Generally for loading from persistent storage
    public BukkitCauldron(Map<Ingredient<ItemStack>, Integer> ingredients, Block block, long brewStart) {
        this.ingredients = ingredients;
        this.block = block;
        this.brewStart = brewStart;

        TheBrewingProject.getInstance().getBreweryRegistry().addActiveCauldron(this);
    }


    @Override
    public void tick() {
        if (!BlockUtil.isChunkLoaded(block)) {
            return;
        }

        if (!this.isOnHeatSource()) {
            this.remove();
            return;
        }
        getBrew()
                .flatMap(brew -> brew.closestRecipe(TheBrewingProject.getInstance().getRecipeRegistry()))
                .ifPresent(reducedRecipe -> this.closestRecipe = reducedRecipe);
        this.updateParticleColor();
        this.playBrewingEffects();
    }


    public void remove() {
        ListenerUtil.removeCauldron(this, TheBrewingProject.getInstance().getBreweryRegistry(), TheBrewingProject.getInstance().getDatabase());
    }


    public boolean addIngredient(@NotNull ItemStack item, Player player) {
        // TODO: Add API event
        // TODO: Add permission check
        if (ingredients.isEmpty()) {
            this.brewStart = block.getWorld().getGameTime();
        }
        Ingredient<ItemStack> ingredient = BukkitIngredientManager.INSTANCE.getIngredient(item);
        int amount = ingredients.computeIfAbsent(ingredient, ignored -> 0);
        ingredients.put(ingredient, amount + 1);
        item.setAmount(item.getAmount() - 1);
        return true;
    }


    public void updateParticleColor() {
        if (this.closestRecipe == null && this.particleColor != Color.AQUA) {
            this.particleColor = Color.AQUA;
        } else if (this.closestRecipe != null) {
            this.particleColor = ColorUtil.getNextColor(particleColor, this.closestRecipe.getRecipeResult().getColor(), block.getWorld().getGameTime() - brewStart, this.closestRecipe.getBrewTime());
        }
    }


    public boolean isOnHeatSource() {
        if (Config.HEAT_SOURCES.isEmpty()) {
            return true;
        }

        Block blockBelow = block.getRelative(0, -1, 0);
        Material below = blockBelow.getType();
        if (below == Material.CAMPFIRE || below == Material.SOUL_CAMPFIRE) {
            return BlockUtil.isLitCampfire(blockBelow);
        } else if (below == Material.LAVA || below == Material.WATER) {
            return BlockUtil.isSource(blockBelow);
        }
        return Config.HEAT_SOURCES.contains(below.name().toLowerCase());
    }


    public boolean cauldronTypeMatchesRecipe() {
        if (closestRecipe == null) {
            return false;
        }
        return closestRecipe.getCauldronType().materialKey().equals(block.getType().getKey().toString());
    }

    public void playBrewingEffects() {
        Location particleLoc = // Complex particle location based off BreweryX
                block.getLocation().add(0.5 + (RANDOM.nextDouble() * 0.8 - 0.4), 0.9, 0.5 + (RANDOM.nextDouble() * 0.8 - 0.4));

        block.getWorld().spawnParticle(Particle.SPELL_MOB, particleLoc, 0, particleColor);


        if (!Config.MINIMAL_PARTICLES) {
            return;
        }

        if (RANDOM.nextFloat() > 0.85) {
            // Dark pixely smoke cloud at 0.4 random in x and z
            // 0 count enables direction, send to y = 1 with speed 0.09
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, particleLoc, 0, 0, 1, 0, 0.09);
        }
        if (RANDOM.nextFloat() > 0.2) {
            // A Water Splash with 0.2 offset in x and z
            block.getWorld().spawnParticle(Particle.WATER_SPLASH, particleLoc, 1, 0.2, 0, 0.2);
        }
        if (RANDOM.nextFloat() > 0.4) {
            // Two hovering pixely dust clouds, a bit of offset and with DustOptions to give some color and size
            block.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 2, 0.15, 0.2, 0.15, new Particle.DustOptions(particleColor, 1.5f));
        }
    }

    public Optional<Brew<ItemStack>> getBrew() {
        if (ingredients.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
                new Brew<>(new Interval(brewStart, block.getWorld().getGameTime()), new HashMap<>(ingredients), null, 0, CauldronType.from(block.getType().name()), null)
        );
    }

    public static boolean isValidStructure(Block cauldronBlock) {
        return Tag.CAULDRONS.isTagged(cauldronBlock.getType()) && isHeatSource(cauldronBlock.getRelative(BlockFace.DOWN).getType())
                && cauldronBlock.getBlockData() instanceof Levelled levelled && levelled.getLevel() > 0;
    }

    private static boolean isHeatSource(Material material) {
        return Tag.FIRE.isTagged(material) || Tag.CAMPFIRES.isTagged(material) || material == Material.LAVA;
    }

    @Override
    public BreweryLocation position() {
        return new BreweryLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
    }

    @Override
    public long brewStart() {
        return brewStart;
    }

    @Override
    public Map<Ingredient<ItemStack>, Integer> ingredients() {
        return ingredients;
    }
}
