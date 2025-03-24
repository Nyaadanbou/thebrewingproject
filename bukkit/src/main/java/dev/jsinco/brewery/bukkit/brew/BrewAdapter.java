package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.breweries.BarrelPdcType;
import dev.jsinco.brewery.bukkit.breweries.CauldronPdcType;
import dev.jsinco.brewery.bukkit.ingredient.IngredientsPdcType;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.bukkit.util.MomentPdcType;
import dev.jsinco.brewery.recipes.DefaultRecipe;
import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.ItemColorUtil;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Moment;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BrewAdapter {


    private static final int DATA_VERSION = 0;
    private static final NamespacedKey BREW_TIME = NamespacedKey.fromString(Registry.brewerySpacedKey("brew_time"));
    private static final NamespacedKey INGREDIENTS = NamespacedKey.fromString(Registry.brewerySpacedKey("ingredients"));
    private static final NamespacedKey AGING = NamespacedKey.fromString(Registry.brewerySpacedKey("aging_time"));
    private static final NamespacedKey DISTILL_RUNS = NamespacedKey.fromString(Registry.brewerySpacedKey("distill_runs"));
    private static final NamespacedKey CAULDRON_TYPE = NamespacedKey.fromString(Registry.brewerySpacedKey("cauldron_type"));
    private static final NamespacedKey BARREL_TYPE = NamespacedKey.fromString(Registry.brewerySpacedKey("barrel_type"));
    private static final NamespacedKey BREWERY_DATA_VERSION = NamespacedKey.fromString(Registry.brewerySpacedKey("version"));

    public static ItemStack toItem(Brew<ItemStack> brew) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        applyMeta(potionMeta, brew);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    public static void applyMeta(PotionMeta meta, Brew<ItemStack> brew) {
        RecipeRegistry<RecipeResult, ItemStack, PotionMeta> recipeRegistry = TheBrewingProject.getInstance().getRecipeRegistry();
        Optional<Recipe<RecipeResult, ItemStack>> recipe = brew.closestRecipe(recipeRegistry);
        Optional<PotionQuality> quality = recipe.flatMap(brew::quality);

        if (quality.isEmpty()) {
            DefaultRecipe<ItemStack, PotionMeta> randomDefault = recipeRegistry.getRandomDefaultRecipe();
            boolean hasPreviousData = fillPersistentData(meta, brew);
            if (hasPreviousData) {
                return;
            }
            randomDefault.applyMeta(meta);
        } else if (!brew.hasCompletedRecipe(recipe.get())) {
            fillPersistentData(meta, brew);
            incompletePotion(meta, brew);
        } else {
            fillPersistentData(meta, brew);
            recipe.get().getRecipeResult().applyMeta(quality.get(), meta);
        }
    }

    private static void incompletePotion(PotionMeta meta, Brew<ItemStack> brew) {
        int r = 0;
        int g = 0;
        int b = 0;
        int amount = 0;
        Ingredient<ItemStack> topIngredient = null;
        int topIngredientAmount = 0;
        for (Map.Entry<Ingredient<ItemStack>, Integer> ingredient : brew.ingredients().entrySet()) {
            if (topIngredientAmount < ingredient.getValue()) {
                topIngredient = ingredient.getKey();
                topIngredientAmount = ingredient.getValue();
            }
            String key = ingredient.getKey().getKey();
            Color color = ItemColorUtil.getItemColor(key);
            if (color == null) {
                continue;
            }
            r += color.getRed() * ingredient.getValue();
            g += color.getGreen() * ingredient.getValue();
            b += color.getBlue() * ingredient.getValue();
            amount += ingredient.getValue();
        }
        String displayName;
        if (brew.aging() != null && brew.aging().moment() > Moment.AGING_YEAR / 2) {
            displayName = topIngredient == null ? "Aged brew" : "Aged " + topIngredient.displayName().toLowerCase() + " brew";
        } else if (brew.distillRuns() > 0) {
            displayName = topIngredient == null ? "Distillate" : topIngredient.displayName() + " distillate";
        } else {
            displayName = topIngredient == null ? "Fermented mesh" : "Fermented " + topIngredient.displayName().toLowerCase();
        }
        meta.setDisplayName(displayName);
        meta.setColor(org.bukkit.Color.fromRGB(r / amount, g / amount, b / amount));
    }

    private static boolean fillPersistentData(PotionMeta potionMeta, Brew<ItemStack> brew) {
        PersistentDataContainer data = potionMeta.getPersistentDataContainer();
        if (brew.brewTime() != null) {
            data.set(BREW_TIME, MomentPdcType.INSTANCE, brew.brewTime());
        }
        data.set(INGREDIENTS, IngredientsPdcType.INSTANCE, brew.ingredients());
        if (brew.aging() != null) {
            data.set(AGING, MomentPdcType.INSTANCE, brew.aging());
        }
        data.set(DISTILL_RUNS, PersistentDataType.INTEGER, brew.distillRuns());
        if (brew.barrelType() != null) {
            data.set(BARREL_TYPE, BarrelPdcType.INSTANCE, brew.barrelType());
        }
        if (brew.cauldronType() != null) {
            data.set(CAULDRON_TYPE, CauldronPdcType.INSTANCE, brew.cauldronType());
        }
        boolean previouslyStored = data.get(BREWERY_DATA_VERSION, PersistentDataType.INTEGER) != null;
        data.set(BREWERY_DATA_VERSION, PersistentDataType.INTEGER, DATA_VERSION);
        return previouslyStored;
    }

    public static Optional<Brew<ItemStack>> fromItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        Integer dataVersion = data.get(BREWERY_DATA_VERSION, PersistentDataType.INTEGER);
        if (!Objects.equals(dataVersion, DATA_VERSION)) {
            return Optional.empty();
        }
        Moment cauldronTime = data.get(BREW_TIME, MomentPdcType.INSTANCE);
        Map<Ingredient<ItemStack>, Integer> ingredients = data.get(INGREDIENTS, IngredientsPdcType.INSTANCE);
        Moment aging = data.get(AGING, MomentPdcType.INSTANCE);
        Integer distillAmount = data.get(DISTILL_RUNS, PersistentDataType.INTEGER);
        BarrelType barrelType = data.get(BARREL_TYPE, BarrelPdcType.INSTANCE);
        CauldronType cauldronType = data.get(CAULDRON_TYPE, CauldronPdcType.INSTANCE);
        if (ingredients == null) {
            return Optional.empty();
        }
        return Optional.of(new Brew<>(cauldronTime, ingredients, aging, distillAmount == null ? 0 : distillAmount, cauldronType, barrelType));
    }
}
