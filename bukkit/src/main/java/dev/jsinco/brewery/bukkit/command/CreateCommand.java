package dev.jsinco.brewery.bukkit.command;

import com.google.common.collect.Streams;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;
import java.util.function.BiFunction;

public class CreateCommand {


    public static boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission("brewery.command.create")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
            return true;
        }
        if (args.length == 1) {
            Optional<Recipe<ItemStack, PotionMeta>> recipeOptional = TheBrewingProject.getInstance().getRecipeRegistry().getRecipe(args[0]);
            if (recipeOptional.isEmpty()) {
                return false;
            }
            Recipe<ItemStack, PotionMeta> recipe = recipeOptional.get();
            ItemStack brewItem = BrewAdapter.toItem(new Brew<>(new PassedMoment(recipe.getBrewTime()), recipe.getIngredients(), new PassedMoment(recipe.getAgingYears()), recipe.getDistillRuns(), recipe.getCauldronType(), recipe.getBarrelType()));
            player.getWorld().dropItem(player.getLocation(), brewItem);
            return true;
        }
        Map<String, BiFunction<Iterator<String>, Brew<ItemStack>, Brew<ItemStack>>> operators = Map.of(
                "--age", CreateCommand::modifyAge,
                "--distill", CreateCommand::modifyDistill,
                "--cook", CreateCommand::modifyCook,
                "--ingredients", CreateCommand::modifyIngredients
        );
        Map<String, String> replacements = Map.of(
                "-a", "--age",
                "-d", "--distill",
                "-c", "--cook",
                "-i", "--ingredients"
        );
        Set<String> mandatory = new HashSet<>(Set.of("--cook", "--ingredients"));
        Iterator<String> iterator = Arrays.stream(args).iterator();
        Brew<ItemStack> brew = new Brew<>(null, Map.of(), null, 0, null, null);
        Set<String> used = new HashSet<>();
        while (iterator.hasNext()) {
            String operatorName = iterator.next();
            if (replacements.containsKey(operatorName)) {
                operatorName = replacements.get(operatorName);
            }
            if (used.contains(operatorName)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNIQUE_ARGUMENT, Placeholder.unparsed("argument", operatorName)));
                return false;
            }
            BiFunction<Iterator<String>, Brew<ItemStack>, Brew<ItemStack>> operator = operators.get(operatorName);
            if (operator == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNKNOWN_ARGUMENT, Placeholder.unparsed("argument", operatorName)));
                return false;
            }
            brew = operator.apply(iterator, brew);
            mandatory.remove(operatorName);
            used.add(operatorName);
        }
        if (!mandatory.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_MISSING_MANDATORY_ARGUMENT, Placeholder.unparsed("arguments", mandatory.toString())));
            return false;
        }
        ItemStack brewItem = BrewAdapter.toItem(brew);
        player.getWorld().dropItem(player.getLocation(), brewItem);
        return true;
    }

    private static Brew<ItemStack> modifyIngredients(Iterator<String> arguments, Brew<ItemStack> initial) {
        List<String> ingredientsStrings = Streams.stream(arguments).toList();
        Map<Ingredient<ItemStack>, Integer> ingredients = BukkitIngredientManager.INSTANCE.getIngredientsWithAmount(ingredientsStrings);
        return initial.withIngredients(ingredients);
    }

    private static Brew<ItemStack> modifyCook(Iterator<String> arguments, Brew<ItemStack> initial) {
        long cookTime = (long) (Double.parseDouble(arguments.next()) * Moment.MINUTE);
        return initial.withCauldronTime(new PassedMoment(cookTime)).withCauldronType(CauldronType.WATER);
    }

    private static Brew<ItemStack> modifyDistill(Iterator<String> arguments, Brew<ItemStack> initial) {
        int distillAmount = Integer.parseInt(arguments.next());
        return initial.withDistillAmount(distillAmount);
    }

    private static Brew<ItemStack> modifyAge(Iterator<String> arguments, Brew<ItemStack> initial) {
        long age = (long) (Double.parseDouble(arguments.next()) * Moment.AGING_YEAR);
        BarrelType barrelType = Registry.BARREL_TYPE.get(BreweryKey.parse(arguments.next().toUpperCase(Locale.ROOT)));
        return initial.withAging(new PassedMoment(age)).withBarrelType(barrelType);
    }

    private static boolean onCreateSealed(Player player, String[] args) {
        return false;
    }
}
