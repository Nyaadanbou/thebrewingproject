package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.bukkit.util.ColorUtil;
import dev.jsinco.brewery.recipes.RecipeReader;
import dev.jsinco.brewery.recipes.RecipeResultReader;
import dev.jsinco.brewery.util.Util;
import dev.jsinco.brewery.util.moment.Interval;
import org.bukkit.potion.PotionEffectType;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class BukkitRecipeResultReader implements RecipeResultReader<RecipeResult>{
    @Override
    public RecipeResult readRecipeResult(ConfigurationSection configurationSection) {
        return new RecipeResult.Builder()
                .recipeEffects(getRecipeEffects(configurationSection))
                .customModelData(configurationSection.getInt("potion-attributes.custom-model-data", -1))
                .lore(RecipeReader.getQualityFactoredList(configurationSection.getStringList("potion-attributes.lore")))
                .glint(configurationSection.getBoolean("potion-attributes.glint", false))
                .names(RecipeReader.getQualityFactoredString(configurationSection.getString("potion-attributes.name")))
                .color(ColorUtil.parseColorString(configurationSection.getString("potion-attributes.color")))
                .build();
    }
    private static RecipeEffects getRecipeEffects(ConfigurationSection configurationSection) {
        return new RecipeEffects.Builder()
                .actionBar(configurationSection.getString("messages.action-bar", null))
                .title(configurationSection.getString("messages.title", null))
                .message(configurationSection.getString("messages.message", null))
                .commands(RecipeReader.getQualityFactoredList(configurationSection.getStringList("commands")))
                .effects(getEffectsFromStringList(configurationSection.getStringList("effects")))
                .alcohol(RecipeReader.parseAlcoholString(configurationSection.getString("alcohol", "0%")))
                .build();
    }

    private static RecipeEffect getEffect(String string) {
        if (!string.contains("/")) {
            return RecipeEffect.of(PotionEffectType.getByName(string), new Interval(1, 1), new Interval(1, 1));
        }

        String[] parts = string.split("/");
        PotionEffectType effectType = PotionEffectType.getByName(parts[0]);
        Interval durationBounds = Interval.parse(parts[1]);
        Interval amplifierBounds;
        if (parts.length >= 3) {
            amplifierBounds = Interval.parse(parts[2]);
        } else {
            amplifierBounds = new Interval(1, 1);
        }

        return RecipeEffect.of(effectType, durationBounds, amplifierBounds);
    }

    private static List<RecipeEffect> getEffectsFromStringList(List<String> list) {
        List<RecipeEffect> effects = new ArrayList<>();
        for (String string : list) {
            effects.add(getEffect(string));
        }
        return effects;
    }

}
