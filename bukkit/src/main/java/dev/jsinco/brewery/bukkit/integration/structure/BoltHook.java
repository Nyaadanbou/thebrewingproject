package dev.jsinco.brewery.bukkit.integration.structure;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.popcraft.bolt.BoltAPI;

public class BoltHook {

    private static final boolean ENABLED = checkAvailable();
    private static BoltAPI boltAPI;

    private static boolean checkAvailable() {
        try {
            Class.forName("org.popcraft.bolt.BoltAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasAccess(Block block, Player player) {
        if (!ENABLED) {
            return true;
        }
        if (boltAPI == null) {
            return true;
        }
        return boltAPI.canAccess(block, player);
    }

    public static void initiate() {
        boltAPI = Bukkit.getServer().getServicesManager().load(BoltAPI.class);
    }
}
