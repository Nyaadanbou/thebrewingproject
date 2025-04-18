package dev.jsinco.brewery.bukkit.integration.structure;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StructureAccessHook {

    public static boolean hasAccess(Block block, Player player) {
        return WorldGuardHook.hasAccess(block, player)
                && GriefPreventionHook.hasAccess(block, player)
                && TownyHook.hasAccess(block, player);
    }
}
