package dev.jsinco.brewery.bukkit.integration.item;

import cc.mewcraft.wakame.api.Koish;
import cc.mewcraft.wakame.api.item.KoishItem;
import dev.jsinco.brewery.bukkit.api.integration.ItemIntegration;
import dev.jsinco.brewery.util.ClassUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 实现了 {@link ItemIntegration} 以便让 TheBrewingProject 识别 Koish 的物品.
 * <p>
 * 在 TheBrewingProject 中这样使用:
 * - `koish:example` -> id 为 koish:example 的 Koish 物品
 * - `koish:dir/example` -> id 为 koish:dir/example 的 Koish 物品
 */
public class KoishIntegration implements ItemIntegration {

    private static final boolean ENABLED = ClassUtil.exists("cc.mewcraft.wakame.api.Koish");
    private static final CompletableFuture<Void> INITIALIZED_FUTURE = CompletableFuture.completedFuture(null);

    @Override
    public Optional<ItemStack> createItem(String id) {
        return Optional.ofNullable(Koish.get().getItemRegistry().getOrNull(Key.key(id)))
                .map(KoishItem::createItemStack);
    }

    @Override
    public boolean isIngredient(String id) {
        return Koish.get().getItemRegistry().getOrNull(Key.key(id)) != null;
    }

    @Override
    public @Nullable Component displayName(String id) {
        return Optional.ofNullable(Koish.get().getItemRegistry().getOrNull(Key.key(id)))
                .map(KoishItem::getName)
                .orElse(null);
    }

    @Override
    public @Nullable String getItemId(ItemStack itemStack) {
        return Optional.ofNullable(Koish.get().getItemRegistry().getOrNull(itemStack))
                .map(KoishItem::getId)
                .map(Key::value)
                .map(str -> str.replace('/', ':'))
                .orElse(null);
    }

    @Override
    public CompletableFuture<Void> initialized() {
        return INITIALIZED_FUTURE;
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

    @Override
    public String getId() {
        return "koish";
    }

}
