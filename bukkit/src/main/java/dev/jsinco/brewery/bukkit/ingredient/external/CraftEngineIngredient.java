package dev.jsinco.brewery.bukkit.ingredient.external;

import dev.jsinco.brewery.bukkit.integration.item.CraftEngineHook;
import dev.jsinco.brewery.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;

import java.util.Objects;
import java.util.Optional;

public class CraftEngineIngredient implements Ingredient {

    private final String key;

    private CraftEngineIngredient(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String displayName() {
        return CraftEngineHook.displayName(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CraftEngineIngredient that = (CraftEngineIngredient) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    public static Optional<Ingredient> from(String craftEngineId) {
        BreweryKey breweryKey = BreweryKey.parse(craftEngineId);
        if (!breweryKey.namespace().equals("craftengine")) {
            return Optional.empty();
        }
        return Optional.of(new CraftEngineIngredient(breweryKey.key()));
    }
}
