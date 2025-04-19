package dev.jsinco.brewery.bukkit.integration.structure;

import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class HuskClaimsHook {

    private static final boolean ENABLED = checkAvailable();

    private static boolean checkAvailable() {
        try {
            Class.forName("com.palmergames.bukkit.towny.utils.PlayerCacheUtil");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasAccess(Block block, Player player) {
        if (!ENABLED) {
            return true;
        }
        BukkitHuskClaimsAPI huskClaimsAPI = BukkitHuskClaimsAPI.getInstance();
        return huskClaimsAPI.isOperationAllowed(huskClaimsAPI.getOnlineUser(player), OperationType.CONTAINER_OPEN, huskClaimsAPI.getPosition(block.getLocation()));
    }
}
