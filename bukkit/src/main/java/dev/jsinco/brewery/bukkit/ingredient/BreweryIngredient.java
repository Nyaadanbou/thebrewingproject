package dev.jsinco.brewery.bukkit.ingredient;

import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class BreweryIngredient implements Ingredient {
    protected final BreweryKey ingredientKey;
    private final String displayName;

    public BreweryIngredient(BreweryKey ingredientKey, String displayName) {
        this.ingredientKey = ingredientKey;
        this.displayName = displayName;
    }

    @Override
    public String getKey() {
        return ingredientKey.toString();
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BreweryIngredient that = (BreweryIngredient) o;
        return Objects.equals(ingredientKey, that.ingredientKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ingredientKey);
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
        String displayName = dataContainer.get(BrewAdapter.BREWERY_DISPLAY_NAME, PersistentDataType.STRING);
        BreweryKey breweryKey = BreweryKey.parse(key);
        displayName = displayName == null ? breweryKey.key() : displayName;
        if (score != null) {
            return Optional.of(new ScoredBreweryIngredient(breweryKey, score, displayName));
        }
        return Optional.of(new BreweryIngredient(breweryKey, displayName));
    }

    public static Optional<Ingredient> from(String id) {
        BreweryKey breweryKey = BreweryKey.parse(id);
        if (!breweryKey.namespace().equals("brewery")) {
            return Optional.empty();
        }
        return Optional.of(new BreweryIngredient(breweryKey, breweryKey.key()));
    }
}
