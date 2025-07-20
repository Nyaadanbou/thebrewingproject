package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.NamedDrunkEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HallucinationNamedExecutable implements ExecutableEventStep {

    private static final int BLOCK_RANGE = 20;

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) return;

        Block block = player.getTargetBlock(null, BLOCK_RANGE);
        // Random material, keep looping until we get a material that can be a block
        Material material;
        do {
            material = Material.values()[RANDOM.nextInt(Material.values().length)];
        } while (!material.isBlock() || material.isAir() || material.isLegacy());

        BlockData blockData = material.createBlockData();

        player.sendBlockChange(block.getLocation(), blockData);
        player.spawnParticle(Particle.DUST, block.getLocation().toCenterLocation(), 10, 0.5, 0.5, 0.5, new Particle.DustOptions(blockData.getMapColor(), 1f));
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(NamedDrunkEvent.HALLUCINATION, HallucinationNamedExecutable::new);
    }
}
