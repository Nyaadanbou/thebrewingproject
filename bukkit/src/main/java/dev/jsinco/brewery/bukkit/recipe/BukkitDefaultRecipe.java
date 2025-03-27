package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.recipes.DefaultRecipe;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation of a default recipe used when no recipe is found while brewing
 */
@Getter
public class BukkitDefaultRecipe implements DefaultRecipe<ItemStack, PotionMeta> {

    private final String name;
    private final List<String> lore;
    private final Color color;
    private final int customModelData;
    private final boolean glint;

    public BukkitDefaultRecipe(String name, List<String> lore, Color color, int customModelData, boolean glint) {
        this.name = name == null ? "Cauldron Brew" : name;
        this.lore = lore == null ? new ArrayList<>() : lore;
        this.color = color == null ? Color.BLUE : color;
        this.customModelData = customModelData;
        this.glint = glint;
    }

    public ItemStack newBrewItem() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        applyMeta(meta);
        potion.setItemMeta(meta);
        return potion;
    }

    public void applyMeta(PotionMeta meta) {
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        meta.setColor(color);
        if (glint) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
    }

    public static class Builder {
        private String name = "Cauldron Brew";
        private List<String> lore = new ArrayList<>();
        private Color color = Color.BLUE;
        private int customModelData = -1;
        private boolean glint = false;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder glint(boolean glint) {
            this.glint = glint;
            return this;
        }

        public BukkitDefaultRecipe build() {
            return new BukkitDefaultRecipe(name, lore, color, customModelData, glint);
        }
    }
}
