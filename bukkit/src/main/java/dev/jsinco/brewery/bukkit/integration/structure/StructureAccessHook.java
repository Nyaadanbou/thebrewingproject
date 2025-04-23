package dev.jsinco.brewery.bukkit.integration.structure;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StructureAccessHook {

    public static boolean hasAccess(Block block, Player player) {
        return WorldGuardHook.hasAccess(block, player)
                && GriefPreventionHook.hasAccess(block, player)
                && TownyHook.hasAccess(block, player)
                && LandsHook.hasAccess(block, player)
                && HuskClaimsHook.hasAccess(block, player)
                && BoltHook.hasAccess(block, player);
    }

    public static void initiate(TheBrewingProject plugin) {
        LandsHook.initiate(plugin);
        BoltHook.initiate();
    }
}
