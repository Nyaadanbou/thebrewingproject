package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BukkitAdapter {

    public static Location toLocation(BreweryLocation location) {
        return new Location(Bukkit.getWorld(location.worldUuid()), location.x(), location.y(), location.z());
    }

    public static BreweryLocation toBreweryLocation(Location location) {
        return new BreweryLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID());
    }

    public static BreweryLocation toBreweryLocation(Block block) {
        return new BreweryLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
    }
}
