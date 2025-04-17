package dev.jsinco.brewery.bukkit.ingredient;

import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class BreweryIngredient implements Ingredient {
    protected final BreweryKey ingredientKey;

    public BreweryIngredient(BreweryKey ingredientKey) {
        this.ingredientKey = ingredientKey;
    }

    @Override
    public String getKey() {
        return ingredientKey.toString();
    }

    @Override
    public String displayName() {
        return ingredientKey.key();
    }

    public static Optional<Ingredient> from(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return Optional.empty();
        }
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        String key = dataContainer.get(BrewAdapter.BREWERY_TAG, PersistentDataType.STRING);
        if (key == null) {
            return Optional.empty();
        }
        Double score = dataContainer.get(BrewAdapter.BREWERY_SCORE, PersistentDataType.DOUBLE);
        if (score != null) {
            return Optional.of(new ScoredBreweryIngredient(BreweryKey.parse(key), score));
        }
        return Optional.of(new BreweryIngredient(BreweryKey.parse(key)));
    }

    public static Optional<Ingredient> from(String id) {
        return Optional.of(new BreweryIngredient(BreweryKey.parse(id)));
    }
}
