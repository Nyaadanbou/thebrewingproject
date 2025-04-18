package dev.jsinco.brewery.bukkit.integration.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook {

    private static final boolean ENABLED = checkAvailable();

    private ItemsAdderHook() {
        throw new IllegalStateException("Utility class");
    }

    private static boolean checkAvailable() {
        try {
            Class.forName("dev.lone.itemsadder.api.CustomStack");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static @Nullable String itemsAdderId(ItemStack itemStack) {
        if (!ENABLED) {
            return null;
        }
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        return customStack == null ? null : customStack.getId();
    }

    public static @Nullable String displayName(String itemsAdderId) {
        if (!ENABLED) {
            return null;
        }
        CustomStack customStack = CustomStack.getInstance(itemsAdderId);
        return customStack == null ? null : customStack.getDisplayName();
    }

    public static @Nullable ItemStack build(String itemsAdderId) {
        if (!ENABLED) {
            return null;
        }
        CustomStack customStack = CustomStack.getInstance(itemsAdderId);
        return customStack == null ? null : customStack.getItemStack();
    }

    public static boolean isItemsAdder(String itemsAdderId) {
        return ENABLED && CustomStack.isInRegistry(itemsAdderId);
    }

}
