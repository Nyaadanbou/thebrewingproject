package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.bukkit.util.ColorUtil;
import dev.jsinco.brewery.recipes.QualityData;
import dev.jsinco.brewery.recipes.RecipeReader;
import dev.jsinco.brewery.recipes.RecipeResultReader;
import dev.jsinco.brewery.util.moment.Interval;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class BukkitRecipeResultReader implements RecipeResultReader<ItemStack, PotionMeta> {
    @Override
    public BukkitRecipeResult readRecipeResult(ConfigurationSection configurationSection) {
        return new BukkitRecipeResult.Builder()
                .recipeEffects(getRecipeEffects(configurationSection))
                .customModelData(configurationSection.getInt("potion-attributes.custom-model-data", -1))
                .lore(QualityData.readQualityFactoredStringList(configurationSection.getStringList("potion-attributes.lore")))
                .glint(configurationSection.getBoolean("potion-attributes.glint", false))
                .names(QualityData.readQualityFactoredString(configurationSection.getString("potion-attributes.name")))
                .color(ColorUtil.parseColorString(configurationSection.getString("potion-attributes.color")))
                .appendBrewInfoLore(configurationSection.getBoolean("potion-attributes.append-brew-info-lore", true))
                .build();
    }

    private static QualityData<RecipeEffects> getRecipeEffects(ConfigurationSection configurationSection) {
        QualityData<String> actionBar = QualityData.readQualityFactoredString(configurationSection.getString("messages.action-bar", null));
        QualityData<String> title = QualityData.readQualityFactoredString(configurationSection.getString("messages.title", null));
        QualityData<String> message = QualityData.readQualityFactoredString(configurationSection.getString("messages.title", null));
        QualityData<List<String>> commands = QualityData.readQualityFactoredStringList(configurationSection.getStringList("commands"));
        QualityData<List<RecipeEffect>> effects = QualityData.readQualityFactoredStringList(configurationSection.getStringList("effects"))
                .map(list -> list
                        .stream()
                        .map(BukkitRecipeResultReader::getEffect)
                        .toList()
                );
        QualityData<Integer> alcohol = QualityData.readQualityFactoredString(configurationSection.getString("alcohol", "0%"))
                .map(RecipeReader::parseAlcoholString);
        return QualityData.fromValueMapper(quality -> new RecipeEffects.Builder()
                .actionBar(actionBar.get(quality))
                .title(title.get(quality))
                .message(message.get(quality))
                .commands(commands.getOrDefault(quality, List.of()))
                .alcohol(alcohol.getOrDefault(quality, 0))
                .effects(effects.getOrDefault(quality, List.of()))
                .build()
        );
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
