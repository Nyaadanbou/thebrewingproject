package dev.jsinco.brewery.bukkit.integration.structure;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private static final boolean ENABLED = checkAvailable();

    private static boolean checkAvailable() {
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasAccess(Block block, Player player) {
        if (!ENABLED) {
            return true;
        }
        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlugin instance = WorldGuardPlugin.inst();
        if (worldGuard == null || instance == null || !instance.isEnabled()) {
            return true;
        }
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionQuery query = platform.getRegionContainer().createQuery();
        return query.testBuild(BukkitAdapter.adapt(block.getLocation()), instance.wrapPlayer(player), Flags.USE, Flags.CHEST_ACCESS);
    }
}
