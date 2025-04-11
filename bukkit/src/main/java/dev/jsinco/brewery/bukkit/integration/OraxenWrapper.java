package dev.jsinco.brewery.bukkit.integration;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenWrapper {

    private static final boolean ENABLED = checkAvailable();

    private static boolean checkAvailable() {
        try {
            Class.forName("io.th0rgal.oraxen.api.OraxenItems");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private OraxenWrapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static @Nullable String oraxenId(ItemStack itemStack) {
        return ENABLED ? OraxenItems.getIdByItem(itemStack) : null;
    }

    public static @Nullable ItemBuilder itemBuilder(String oraxenId) {
        return ENABLED ? OraxenItems.getItemById(oraxenId) : null;
    }

    public static @Nullable ItemStack build(String oraxenId) {
        if (!ENABLED) {
            return null;
        }
        ItemBuilder itemBuilder = itemBuilder(oraxenId);
        return itemBuilder == null ? null : itemBuilder.build();
    }

    public static boolean isOraxen(String oraxenId) {
        return ENABLED && OraxenWrapper.isOraxen(oraxenId);
    }
}
