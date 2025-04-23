package dev.jsinco.brewery.bukkit.integration.item;

import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CraftEngineHook {

    private static final boolean ENABLED = checkAvailable();

    private static boolean checkAvailable() {
        try {
            Class.forName("net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static @Nullable String id(ItemStack itemStack) {
        if (!ENABLED) {
            return null;
        }
        return BukkitCraftEngine.instance().itemManager().itemId(itemStack).toString();
    }

    public static @Nullable String displayName(String id) {
        if (!ENABLED) {
            return null;
        }
        return BukkitCraftEngine.instance().itemManager().createWrappedItem(Key.from(id), null).customName().orElse(id);
    }

    public static @Nullable ItemStack build(String id) {
        if (!ENABLED) {
            return null;
        }
        return Optional.ofNullable(BukkitCraftEngine.instance().itemManager().createWrappedItem(Key.from(id), null))
                .map(Item::getItem)
                .orElse(null);
    }
}
