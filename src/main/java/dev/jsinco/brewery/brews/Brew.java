package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.DefaultRecipe;
import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.Interval;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record Brew(@Nullable Interval brewTime, @NotNull Map<Ingredient, Integer> ingredients,
                   @Nullable Interval aging, int distillRuns, @Nullable CauldronType cauldronType,
                   @Nullable BarrelType barrelType) {

    private static final NamespacedKey BREW_TIME = Registry.brewerySpacedKey("brew_time");
    private static final NamespacedKey INGREDIENTS = Registry.brewerySpacedKey("ingredients");
    private static final NamespacedKey AGING = Registry.brewerySpacedKey("aging_time");
    private static final NamespacedKey DISTILL_RUNS = Registry.brewerySpacedKey("distill_runs");
    private static final NamespacedKey CAULDRON_TYPE = Registry.brewerySpacedKey("cauldron_type");
    private static final NamespacedKey BARREL_TYPE = Registry.brewerySpacedKey("barrel_type");

    public Brew withCauldronTime(Interval interval) {
        return new Brew(interval, ingredients, aging, distillRuns, cauldronType, barrelType);
    }

    public Brew withIngredients(Map<Ingredient, Integer> ingredients) {
        return new Brew(brewTime, Map.copyOf(ingredients), aging, distillRuns, cauldronType, barrelType);
    }

    public Brew withAging(Interval interval) {
        return new Brew(brewTime, ingredients, interval, distillRuns, cauldronType, barrelType);
    }

    public Brew withDistillAmount(int amount) {
        return new Brew(brewTime, ingredients, aging, amount, cauldronType, barrelType);
    }

    public Brew withCauldronType(CauldronType type) {
        return new Brew(brewTime, ingredients, aging, distillRuns, type, barrelType);
    }

    public Brew withBarrelType(BarrelType type) {
        return new Brew(brewTime, ingredients, aging, distillRuns, cauldronType, type);
    }


    public Optional<Recipe> closestRecipe() {
        RecipeRegistry registry = TheBrewingProject.getInstance().getRecipeRegistry();
        double bestScore = 0;
        Recipe bestMatch = null;
        for (Recipe recipe : registry.getRecipes()) {
            // Don't even bother checking recipes that don't have the same amount of ingredients
            if (this.ingredients.size() != recipe.getIngredients().size()
                    || !this.ingredients.keySet().equals(recipe.getIngredients().keySet())) {
                continue;
            }
            double score = evaluateRecipe(recipe);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }
        return Optional.ofNullable(bestMatch);
    }

    private double evaluateRecipe(Recipe recipe) {
        double ingredientScore = getIngredientScore(recipe.getIngredients(), this.ingredients);
        double cauldronTimeScore = 1;
        if (brewTime != null) {
            if (recipe.getBrewTime() == 0 && brewTime.diff() > 0) {
                cauldronTimeScore = 0;
            }
            cauldronTimeScore = (double) Math.abs(brewTime.diff() - recipe.getBrewTime()) / recipe.getBrewTime();
        }
        double agingTimeScore = 1;
        if (aging != null) {
            if (recipe.getAgingYears() == 0 && aging.diff() > 0) {
                agingTimeScore = 0;
            }
            agingTimeScore = (double) Math.abs(aging.diff() - recipe.getAgingYears()) / recipe.getAgingYears();
        }
        double cauldronTypeScore = 1;
        if (cauldronType != null) {
            if (!cauldronType.equals(recipe.getCauldronType())) {
                cauldronTypeScore = 0.9;
            }
        }
        double barrelTypeScore = 1;
        if (barrelType != null) {
            if (!barrelType.equals(recipe.getBarrelType())) {
                barrelTypeScore = 0.9;
            }
        }
        return ingredientScore * cauldronTimeScore * agingTimeScore * cauldronTypeScore * barrelTypeScore;
    }

    private double getIngredientScore(Map<Ingredient, Integer> target, Map<Ingredient, Integer> actual) {
        double score = 1;
        for (Map.Entry<Ingredient, Integer> targetEntry : target.entrySet()) {
            Integer actualAmount = actual.get(targetEntry.getKey());
            if (actualAmount == null) {
                return 0;
            }
            score *= (double) Math.abs(targetEntry.getValue() - actualAmount) / targetEntry.getValue();
        }
        return score;
    }

    public Optional<PotionQuality> quality(Recipe recipe) {
        double score = evaluateRecipe(recipe);
        double scoreWithDifficulty;
        if (recipe.getBrewDifficulty() == 0) {
            return Optional.of(PotionQuality.EXCELLENT);
        }
        // Avoid extreme point in calculation (can not divide by 0)
        if (recipe.getBrewDifficulty() == 1) {
            scoreWithDifficulty = score;
        } else {
            double logBrewDifficulty = Math.log(recipe.getBrewDifficulty());
            scoreWithDifficulty = (Math.exp(score * logBrewDifficulty) / Math.exp(logBrewDifficulty) - 1D / recipe.getBrewDifficulty()) / (1 - 1D / recipe.getBrewDifficulty());
        }
        if (scoreWithDifficulty > 0.9) {
            return Optional.of(PotionQuality.EXCELLENT);
        }
        if (scoreWithDifficulty > 0.5) {
            return Optional.of(PotionQuality.GOOD);
        }
        if (scoreWithDifficulty > 0.3) {
            return Optional.of(PotionQuality.BAD);
        }
        return Optional.empty();
    }

    public boolean hasCompletedRecipe(Recipe recipe) {
        if (!recipe.getIngredients().keySet().equals(ingredients)) {
            return false;
        }
        if (recipe.getBrewTime() > 0 && (brewTime == null || brewTime.diff() < 1)) {
            return false;
        }
        if (recipe.getAgingYears() > 0 && (aging == null || aging.diff() < 1)) {
            return false;
        }
        return recipe.getDistillRuns() <= 0 || distillRuns >= 1;
    }

    public ItemStack toItem() {
        // Todo - What needs to happen here:
        // this should check if the closest recipe is not null
        // if it's not null, we get the Recipe from our ReducedRecipe and create the potion
        Optional<Recipe> recipe = closestRecipe();
        Optional<PotionQuality> quality = recipe.flatMap(this::quality);
        if (quality.isEmpty()) {
            DefaultRecipe randomDefault = TheBrewingProject.getInstance().getRecipeRegistry().getRandomDefaultRecipe();
            return randomDefault.newBrewItem();
        } else if (!hasCompletedRecipe(recipe.get())) {
            return incompletePotion();
        } else {
            return recipe.get().getRecipeResult().newBrewItem(quality.get());
        }
    }


    /**
     * Set the meta for an incomplete potion. This means that the cauldron found a recipe, but it requires aging or distilling
     */
    private ItemStack incompletePotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setDisplayName("Unfinished Brew");
        meta.setColor(Util.getRandomElement(Util.NAME_TO_COLOR_MAP.values().stream().toList()));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (brewTime != null) {
            data.set(BREW_TIME, Interval.PDC_TYPE, brewTime);
        }
        data.set(INGREDIENTS, Ingredient.PDC_TYPE, ingredients);
        if (aging != null) {
            data.set(AGING, Interval.PDC_TYPE, aging);
        }
        data.set(DISTILL_RUNS, PersistentDataType.INTEGER, distillRuns);
        if (barrelType != null) {
            data.set(BARREL_TYPE, BarrelType.PDC_TYPE, barrelType);
        }
        if (cauldronType != null) {
            data.set(CAULDRON_TYPE, CauldronType.PDC_TYPE, cauldronType);
        }
        potion.setItemMeta(meta);
        return potion;
    }

    public static Optional<Brew> fromItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        Interval cauldronTime = data.get(BREW_TIME, Interval.PDC_TYPE);
        Map<Ingredient, Integer> ingredients = data.get(INGREDIENTS, Ingredient.PDC_TYPE);
        Interval aging = data.get(AGING, Interval.PDC_TYPE);
        Integer distillAmount = data.get(DISTILL_RUNS, PersistentDataType.INTEGER);
        BarrelType barrelType = data.get(BARREL_TYPE, BarrelType.PDC_TYPE);
        CauldronType cauldronType = data.get(CAULDRON_TYPE, CauldronType.PDC_TYPE);
        if (ingredients == null) {
            return Optional.empty();
        }
        return Optional.of(new Brew(cauldronTime, ingredients, aging, distillAmount == null ? 0 : distillAmount, cauldronType, barrelType));
    }
}
