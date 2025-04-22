package dev.jsinco.brewery.bukkit.integration.item;

import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewImpl;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.brew.BrewQuality;
import dev.jsinco.brewery.recipes.BrewScoreImpl;
import dev.jsinco.brewery.util.BreweryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
        // ChestShops api does currently not allow us to validate whether an item matches (2025/04/19)
        // So going to void the implementation for now
        // Bukkit.getPluginManager().registerEvents(new ChestShopHook(), plugin);
    }

    @EventHandler
    public void onItemStringQueryEvent(ItemStringQueryEvent event) {
        ItemMeta itemMeta = event.getItem().getItemMeta();
        if (itemMeta == null) {
            return;
        }
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        Optional<Brew> brew = BrewAdapter.fromItem(event.getItem());
        if (brew.isPresent()) {
            return;
        }
        if (!dataContainer.has(BrewAdapter.BREWERY_TAG)) {
            return;
        }
        String output = BreweryKey.parse(dataContainer.get(BrewAdapter.BREWERY_TAG, PersistentDataType.STRING)).key();
        if (dataContainer.has(BrewAdapter.BREWERY_SCORE)) {
            BrewQuality quality = BrewScoreImpl.quality(dataContainer.get(BrewAdapter.BREWERY_SCORE, PersistentDataType.DOUBLE));
            if (quality != null) {
                String extra = switch (quality) {
                    case BAD -> "+";
                    case GOOD -> "++";
                    case EXCELLENT -> "+++";
                };
                output = extra + output;
            }
        }
        event.setItemString(output);
    }

    @EventHandler
    public void onItemParse(ItemParseEvent event) {
        TheBrewingProject.getInstance().getRecipeRegistry().getRecipe(event.getItemString().replaceAll("^\\+{3}", ""))
                .ifPresent(recipe -> {
                            ItemStack itemStack = BrewAdapter.toItem(new BrewImpl(recipe.getSteps()), new BrewImpl.State.Seal(null));
                            event.setItem(itemStack);
                        }
                );
    }
}


