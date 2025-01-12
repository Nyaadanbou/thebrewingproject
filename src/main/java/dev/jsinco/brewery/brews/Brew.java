package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.recipes.DefaultRecipe;
import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.RecipeRegistry;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.Util;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.Moment;
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
import java.util.Objects;
import java.util.Optional;

public record Brew(@Nullable Moment brewTime, @NotNull Map<Ingredient, Integer> ingredients,
                   @Nullable Moment aging, int distillRuns, @Nullable CauldronType cauldronType,
                   @Nullable BarrelType barrelType) {

    private static final NamespacedKey BREW_TIME = Registry.brewerySpacedKey("brew_time");
    private static final NamespacedKey INGREDIENTS = Registry.brewerySpacedKey("ingredients");
    private static final NamespacedKey AGING = Registry.brewerySpacedKey("aging_time");
    private static final NamespacedKey DISTILL_RUNS = Registry.brewerySpacedKey("distill_runs");
    private static final NamespacedKey CAULDRON_TYPE = Registry.brewerySpacedKey("cauldron_type");
    private static final NamespacedKey BARREL_TYPE = Registry.brewerySpacedKey("barrel_type");
    private static final NamespacedKey BREWERY_DATA_VERSION = Registry.brewerySpacedKey("version");

    private static final int DATA_VERSION = 0;

    public static boolean sameValuesForAging(@NotNull Brew brew1, Brew brew2) {
        if (brew2 == null) {
            return false;
        }
        if (((Interval) brew1.aging).start() != ((Interval) brew2.aging).start()) {
            return false;
        }
        if (brew1.brewTime.moment() != brew2.brewTime.moment()) {
            return false;
        }
        if (!brew1.ingredients.equals(brew2.ingredients)) {
            return false;
        }
        if (brew1.distillRuns != brew2.distillRuns) {
            return false;
        }
        return brew1.cauldronType == brew2.cauldronType && brew1.barrelType == brew2.barrelType;
    }

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
            if (recipe.getBrewTime() == 0 && brewTime.minutes() > 0) {
                cauldronTimeScore = 0;
            } else {
                cauldronTimeScore = getNearbyValueScore(recipe.getBrewTime(), brewTime.minutes());
            }
        }
        double agingTimeScore = 1;
        if (aging != null) {
            if (recipe.getAgingYears() == 0 && aging.agingYears() > 0) {
                agingTimeScore = 0;
            } else {
                agingTimeScore = getNearbyValueScore(recipe.getAgingYears(), aging.agingYears());
            }
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

    private double getNearbyValueScore(long expected, long value) {
        double sigmoid = 1D / (1D + Math.exp((double) (expected - value) / expected));
        return sigmoid * (1D - sigmoid) * 4D;
    }

    private double getIngredientScore(Map<Ingredient, Integer> target, Map<Ingredient, Integer> actual) {
        double score = 1;
        for (Map.Entry<Ingredient, Integer> targetEntry : target.entrySet()) {
            Integer actualAmount = actual.get(targetEntry.getKey());
            if (actualAmount == null) {
                return 0;
            }
            score *= getNearbyValueScore(targetEntry.getValue(), actualAmount);
        }
        return score;
    }

    public Optional<PotionQuality> quality(Recipe recipe) {
        double score = evaluateRecipe(recipe);
        double scoreWithDifficulty;
        // Avoid extreme point, log(0) is minus infinity
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
        if (!recipe.getIngredients().keySet().equals(ingredients.keySet())) {
            return false;
        }
        if (recipe.getBrewTime() > 0 && (brewTime == null || brewTime.minutes() < 1)) {
            return false;
        }
        if (recipe.getAgingYears() > 0 && (aging == null || aging.agingYears() < 1)) {
            return false;
        }
        return recipe.getDistillRuns() <= 0 || distillRuns >= 1;
    }

    public ItemStack toItem() {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        applyMeta(potionMeta);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    public void applyMeta(PotionMeta meta) {
        Optional<Recipe> recipe = closestRecipe();
        Optional<PotionQuality> quality = recipe.flatMap(this::quality);

        if (quality.isEmpty()) {
            DefaultRecipe randomDefault = TheBrewingProject.getInstance().getRecipeRegistry().getRandomDefaultRecipe();
            boolean hasPreviousData = fillPersistentData(meta);
            if (hasPreviousData) {
                return;
            }
            randomDefault.applyMeta(meta);
        } else if (!hasCompletedRecipe(recipe.get())) {
            boolean hasPreviousData = fillPersistentData(meta);
            if (hasPreviousData) {
                return;
            }
            incompletePotion(meta);
        } else {
            recipe.get().getRecipeResult().applyMeta(quality.get(), meta);
        }
    }

    private void incompletePotion(PotionMeta meta) {
        meta.setDisplayName("Unfinished Brew");
        meta.setColor(Util.getRandomElement(Util.NAME_TO_COLOR_MAP.values().stream().toList()));
    }

    private boolean fillPersistentData(PotionMeta potionMeta) {
        PersistentDataContainer data = potionMeta.getPersistentDataContainer();
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
        boolean previouslyStored = data.get(BREWERY_DATA_VERSION, PersistentDataType.INTEGER) != null;
        data.set(BREWERY_DATA_VERSION, PersistentDataType.INTEGER, DATA_VERSION);
        return previouslyStored;
    }

    public static Optional<Brew> fromItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        Integer dataVersion = data.get(BREWERY_DATA_VERSION, PersistentDataType.INTEGER);
        if (!Objects.equals(dataVersion, DATA_VERSION)) {
            return Optional.empty();
        }
        Moment cauldronTime = data.get(BREW_TIME, Moment.PDC_TYPE);
        Map<Ingredient, Integer> ingredients = data.get(INGREDIENTS, Ingredient.PDC_TYPE);
        Moment aging = data.get(AGING, Moment.PDC_TYPE);
        Integer distillAmount = data.get(DISTILL_RUNS, PersistentDataType.INTEGER);
        BarrelType barrelType = data.get(BARREL_TYPE, BarrelType.PDC_TYPE);
        CauldronType cauldronType = data.get(CAULDRON_TYPE, CauldronType.PDC_TYPE);
        if (ingredients == null) {
            return Optional.empty();
        }
        return Optional.of(new Brew(cauldronTime, ingredients, aging, distillAmount == null ? 0 : distillAmount, cauldronType, barrelType));
    }
}
