package dev.jsinco.brewery.bukkit.integration.item;

import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.recipes.BrewScore;
import dev.jsinco.brewery.recipes.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ChestShopHook implements Listener {
    private static final boolean ENABLED = checkAvailable();

    private static boolean checkAvailable() {
        try {
            Class.forName("com.Acrobot.ChestShop.Events.ItemStringQueryEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void initiate(TheBrewingProject plugin) {
        if (!ENABLED) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(new ChestShopHook(), plugin);
    }

    @EventHandler
    public void onItemStringQueryEvent(ItemStringQueryEvent event) {
        Optional<Brew> brewOptional = BrewAdapter.fromItem(event.getItem());
        Optional<Recipe<ItemStack>> recipeOptional = brewOptional.flatMap(brew -> brew.closestRecipe(TheBrewingProject.getInstance().getRecipeRegistry()));
        brewOptional
                .filter(brew -> {
                    BrewScore score = brew.score(recipeOptional.get());
                    return score.completed() && score.brewQuality() != null;
                })
                .ifPresent(brew -> {
                    event.setItemString(recipeOptional.get().getRecipeName());
                });
    }
}


