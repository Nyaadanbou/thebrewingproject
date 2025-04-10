package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.listeners.ListenerUtil;
import dev.jsinco.brewery.bukkit.util.BlockUtil;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BukkitCauldron implements dev.jsinco.brewery.breweries.Cauldron<ItemStack> {

    private static final Random RANDOM = new Random();

    private final Block block;
    @Getter
    private final CauldronType cauldronType;
    @Getter
    private Brew brew;
    private Color particleColor = Color.AQUA;


    public BukkitCauldron(Block block) {
        this(new HashMap<>(), block, block.getWorld().getGameTime());
    }

    public BukkitCauldron(Map<Ingredient<ItemStack>, Integer> ingredients, Block block, long brewStart) {
        this.block = block;
        this.cauldronType = findCauldronType(block);
        this.brew = new Brew(new BrewingStep.Cook(new Interval(brewStart, brewStart), ingredients, cauldronType));
    }

    public BukkitCauldron(Brew brew, Block block) {
        this.block = block;
        this.cauldronType = findCauldronType(block);
        this.brew = brew;
    }

    private static CauldronType findCauldronType(Block block) {
        return Registry.CAULDRON_TYPE.values()
                .stream()
                .filter(cauldronType -> block.getType().getKey().toString().equals(cauldronType.materialKey()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Expected cauldron"));
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
        this.playBrewingEffects();
    }


    public void remove() {
        ListenerUtil.removeCauldron(this, TheBrewingProject.getInstance().getBreweryRegistry(), TheBrewingProject.getInstance().getDatabase());
    }


    public boolean addIngredient(@NotNull ItemStack item, Player player) {
        // TODO: Add API event
        if (!player.hasPermission("brewery.cauldron.access")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.CAULDRON_ACCESS_DENIED));
            return false;
        }
        long gameTime = block.getWorld().getGameTime();
        if (!(brew.lastStep() instanceof BrewingStep.Cook)) {
            brew = brew.withStep(new BrewingStep.Cook(new Interval(gameTime, gameTime), Map.of(BukkitIngredientManager.INSTANCE.getIngredient(item), 1), cauldronType));
            return true;
        }
        brew = brew.witModifiedLastStep(step -> {
            BrewingStep.Cook cook = (BrewingStep.Cook) step;
            Ingredient<ItemStack> ingredient = BukkitIngredientManager.INSTANCE.getIngredient(item);
            Map<Ingredient<?>, Integer> ingredients = new HashMap<>(cook.ingredients());
            int amount = ingredients.computeIfAbsent(ingredient, ignored -> 0);
            ingredients.put(ingredient, amount + 1);
            item.setAmount(item.getAmount() - 1);
            return cook.withIngredients(ingredients);
        });
        return true;
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

    public void playBrewingEffects() {
        Location particleLoc = // Complex particle location based off BreweryX
                block.getLocation().add(0.5 + (RANDOM.nextDouble() * 0.8 - 0.4), 0.9, 0.5 + (RANDOM.nextDouble() * 0.8 - 0.4));

        block.getWorld().spawnParticle(Particle.ENTITY_EFFECT, particleLoc, 0, particleColor);


        if (!Config.MINIMAL_PARTICLES) {
            return;
        }

        if (RANDOM.nextFloat() > 0.85) {
            // Dark pixely smoke cloud at 0.4 random in x and z
            // 0 count enables direction, send to y = 1 with speed 0.09
            block.getWorld().spawnParticle(Particle.LARGE_SMOKE, particleLoc, 0, 0, 1, 0, 0.09);
        }
        if (RANDOM.nextFloat() > 0.2) {
            // A Water Splash with 0.2 offset in x and z
            block.getWorld().spawnParticle(Particle.SPLASH, particleLoc, 1, 0.2, 0, 0.2);
        }
        if (RANDOM.nextFloat() > 0.4) {
            // Two hovering pixely dust clouds, a bit of offset and with DustOptions to give some color and size
            block.getWorld().spawnParticle(Particle.DUST_PLUME, particleLoc, 2, 0.15, 0.2, 0.15, new Particle.DustOptions(particleColor, 1.5f));
        }
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
}
