package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.recipe.BukkitRecipeResult;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.bukkit.util.ListPersistentDataType;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.*;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.ItemColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class BrewAdapter {


    private static final int DATA_VERSION = 0;

    private static final NamespacedKey BREWING_STEPS = BukkitAdapter.toNamespacedKey(BreweryKey.parse("steps"));
    private static final NamespacedKey BREWERY_DATA_VERSION = BukkitAdapter.toNamespacedKey(BreweryKey.parse("version"));
    private static final List<NamespacedKey> PDC_TYPES = List.of(BREWERY_DATA_VERSION);

    public static ItemStack toItem(Brew brew) {
        RecipeRegistry<ItemStack> recipeRegistry = TheBrewingProject.getInstance().getRecipeRegistry();
        Optional<Recipe<ItemStack>> recipe = brew.closestRecipe(recipeRegistry);
        Optional<BrewScore> score = recipe.map(brew::score);
        Optional<BrewQuality> quality = score.flatMap(brewScore -> Optional.ofNullable(brewScore.brewQuality()));
            ItemStack itemStack;
        if (quality.isEmpty()) {
            RecipeResult<ItemStack> randomDefault = recipeRegistry.getRandomDefaultRecipe();
            //TODO Refactor this weird implementation for default recipes
            itemStack = randomDefault.newBrewItem(BrewScore.EXCELLENT, brew);
        } else if (!score.map(BrewScore::completed).get()) {
            itemStack = incompletePotion(brew);
        } else {
            itemStack = recipe.get().getRecipeResult().newBrewItem(score.get(), brew);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        fillPersistentData(itemMeta, brew);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static ItemStack incompletePotion(Brew brew) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        Map<Ingredient<ItemStack>, Integer> ingredients = new HashMap<>();
        for (BrewingStep brewingStep : brew.getSteps()) {
            if (brewingStep instanceof BrewingStep.Cook cook) {
                BukkitIngredientManager.INSTANCE.merge(ingredients, (Map<Ingredient<ItemStack>, Integer>) cook.ingredients());
            }
            if (brewingStep instanceof BrewingStep.Mix mix) {
                BukkitIngredientManager.INSTANCE.merge(ingredients, (Map<Ingredient<ItemStack>, Integer>) mix.ingredients());
            }
        }
        int r = 0;
        int g = 0;
        int b = 0;
        int amount = 0;
        Ingredient<ItemStack> topIngredient = null;
        int topIngredientAmount = 0;
        for (Map.Entry<Ingredient<ItemStack>, Integer> ingredient : ingredients.entrySet()) {
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
        String displayName = switch (brew.getSteps().getLast().stepType()) {
            case COOK ->
                    topIngredient == null ? TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_FERMENTED_UNKNOWN : TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_FERMENTED.replace("<ingredient>", topIngredient.displayName().toLowerCase());
            case DISTILL ->
                    topIngredient == null ? TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_DISTILLED_UNKNOWN : TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_DISTILLED.replace("<ingredient>", topIngredient.displayName());
            case AGE ->
                    topIngredient == null ? TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_AGED_UNKNOWN : TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_AGED.replace("<ingredient>", topIngredient.displayName().toLowerCase());
            case MIX ->
                    topIngredient == null ? TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_MIXED_UNKNOWN : TranslationsConfig.BREW_DISPLAY_NAME_UNFINISHED_MIXED.replace("<ingredient>", topIngredient.displayName());
        };
        potionMeta.displayName(MiniMessage.miniMessage().deserialize(displayName).decoration(TextDecoration.ITALIC, false));
        if (amount != 0) {
            potionMeta.setColor(org.bukkit.Color.fromRGB(r / amount, g / amount, b / amount));
        } else {
            potionMeta.setColor(org.bukkit.Color.YELLOW);
        }
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }

    private static boolean fillPersistentData(ItemMeta itemMeta, Brew brew) {
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        Integer dataVersion = data.get(BREWERY_DATA_VERSION, PersistentDataType.INTEGER);
        boolean previouslyStored = dataVersion != null;
        data.set(BREWING_STEPS, ListPersistentDataType.BREWING_STEP_LIST, brew.getSteps());
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
        return Optional.of(new Brew(data.get(BREWING_STEPS, ListPersistentDataType.BREWING_STEP_LIST)));
    }

    public static void seal(ItemStack itemStack, @Nullable Component volume) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }
        RecipeRegistry<ItemStack> recipeRegistry = TheBrewingProject.getInstance().getRecipeRegistry();
        Optional<Brew> brewOptional = fromItem(itemStack);
        Optional<Recipe<ItemStack>> closestRecipe = brewOptional.flatMap(brew -> brew.closestRecipe(recipeRegistry));
        Optional<BrewScore> score = closestRecipe.map(recipe -> brewOptional.get().score(recipe));
        score.filter(BrewScore::completed)
                .filter(brewScore -> brewScore.brewQuality() != null)
                .map(MessageUtil::getScoreTagResolver)
                .ifPresent(
                        tagResolver -> setSealedLore(volume, closestRecipe.get(), score.get(), tagResolver, itemStack)
                );
    }

    private static void setSealedLore(Component volume, Recipe<ItemStack> closestRecipe, BrewScore score, TagResolver tagResolver, ItemStack itemStack) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Stream<Component> extraLore = Stream.concat(
                volume == null ? Stream.of(Component.empty()) :
                        Stream.of(Component.empty(), miniMessage.deserialize(TranslationsConfig.BREW_TOOLTIP_VOLUME, Placeholder.component("volume", volume))),
                TranslationsConfig.BREW_TOOLTIP_SEALED.stream()
                        .map(line -> miniMessage.deserialize(line, tagResolver))
        );
        List<Component> lore = Stream.concat(
                        ((BukkitRecipeResult) closestRecipe.getRecipeResult()).getLore().get(score.brewQuality())
                                .stream()
                                .map(line -> miniMessage.deserialize(line, tagResolver)),
                        extraLore
                ).map(component1 -> component1.decoration(TextDecoration.ITALIC, false))
                .toList();
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(lore);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        PDC_TYPES.forEach(data::remove);
        itemStack.setItemMeta(meta);
    }
}
