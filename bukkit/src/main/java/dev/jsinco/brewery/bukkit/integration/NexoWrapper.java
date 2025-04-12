package dev.jsinco.brewery.bukkit.integration;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NexoWrapper {

    private static final boolean ENABLED = checkAvailable();

    private NexoWrapper() {
        throw new IllegalStateException("Utility class");
    }

    private static boolean checkAvailable() {
        try {
            Class.forName("com.nexomc.nexo.api.NexoItems");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static @Nullable String nexoId(ItemStack itemStack) {
        if (!ENABLED) {
            return null;
        }
        return NexoItems.idFromItem(itemStack);
    }

    public static @Nullable String displayName(String itemsAdderId) {
        if (!ENABLED) {
            return null;
        }
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemsAdderId);
        if (itemBuilder == null) {
            return null;
        }
        Component displayName = itemBuilder.getDisplayName();
        return displayName == null ? null : PlainTextComponentSerializer.plainText().serialize(displayName);
    }

    public static @Nullable ItemStack build(String itemsAdderId) {
        if (!ENABLED) {
            return null;
        }
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemsAdderId);
        if (itemBuilder == null) {
            return null;
        }
        return itemBuilder.build();
    }

    public static boolean isNexo(String itemsAdderId) {
        return ENABLED && NexoItems.exists(itemsAdderId);
    }
}
