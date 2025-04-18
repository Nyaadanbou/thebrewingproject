package dev.jsinco.brewery.bukkit.integration.structure;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LandsHook {

    private static final boolean ENABLED = checkAvailable();
    private static LandsIntegration landsIntegration;
    private static RoleFlag barrelAccessFlag;

    private static boolean checkAvailable() {
        try {
            Class.forName("me.angeschossen.lands.api.LandsIntegration");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void initiate(TheBrewingProject plugin) {
        if (!ENABLED) {
            return;
        }
        landsIntegration = LandsIntegration.of(plugin);
        barrelAccessFlag = RoleFlag.of(landsIntegration, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "barrel_access")
                .setDisplayName("Barrel Access")
                .setDescription(List.of("§r§7Allows opening", "§r§7BreweryX barrels."))
                .setIcon(new ItemStack(Material.BARREL))
                .setDisplay(true);
    }

    public static boolean hasAccess(Block block, Player player) {
        if (!ENABLED) {
            return true;
        }
        LandWorld lWorld = landsIntegration.getWorld(block.getWorld());
        if (lWorld == null) {
            return true;
        }
        return lWorld.hasRoleFlag(player.getUniqueId(), block.getLocation(), barrelAccessFlag);
    }
}
