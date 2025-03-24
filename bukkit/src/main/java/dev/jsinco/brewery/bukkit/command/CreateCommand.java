package dev.jsinco.brewery.bukkit.command;

import com.google.common.collect.Streams;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.bukkit.recipe.RecipeResult;
import dev.jsinco.brewery.recipes.Recipe;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiFunction;

public class CreateCommand {


    public static boolean onCommand(Player player, String[] args) {
        //TODO check perms
        if (args.length == 1) {
            Optional<Recipe<RecipeResult, ItemStack>> recipeOptional = TheBrewingProject.getInstance().getRecipeRegistry().getRecipe(args[0]);
            if (recipeOptional.isEmpty()) {
                return false;
            }
            Recipe<RecipeResult, ItemStack> recipe = recipeOptional.get();
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
                player.sendMessage("Each argument has to be unique!");
                return false;
            }
            BiFunction<Iterator<String>, Brew<ItemStack>, Brew<ItemStack>> operator = operators.get(operatorName);
            if (operator == null) {
                player.sendMessage("Unknown argument: " + operatorName);
                return false;
            }
            brew = operator.apply(iterator, brew);
            mandatory.remove(operatorName);
            used.add(operatorName);
        }
        if (!mandatory.isEmpty()) {
            player.sendMessage("Missing mandatory argument: " + mandatory);
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
        int cookTime = Integer.parseInt(arguments.next()) * Moment.MINUTE;
        return initial.withCauldronTime(new PassedMoment(cookTime)).withCauldronType(CauldronType.WATER);
    }

    private static Brew<ItemStack> modifyDistill(Iterator<String> arguments, Brew<ItemStack> initial) {
        int distillAmount = Integer.parseInt(arguments.next());
        return initial.withDistillAmount(distillAmount);
    }

    private static Brew<ItemStack> modifyAge(Iterator<String> arguments, Brew<ItemStack> initial) {
        int age = Integer.parseInt(arguments.next()) * Moment.AGING_YEAR;
        BarrelType barrelType = Registry.BARREL_TYPE.get(arguments.next().toUpperCase(Locale.ROOT));
        return initial.withAging(new PassedMoment(age)).withBarrelType(barrelType);
    }

    private static boolean onCreateSealed(Player player, String[] args) {
        return false;
    }
}
