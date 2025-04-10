package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
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
import java.util.function.Function;

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
            ItemStack brewItem = BrewAdapter.toItem(new Brew(recipe.getSteps()));
            player.getWorld().dropItem(player.getLocation(), brewItem);
            return true;
        }
        Map<String, Function<Queue<String>, BrewingStep>> operators = Map.of(
                "--age", CreateCommand::getAge,
                "--distill", CreateCommand::getDistill,
                "--cook", CreateCommand::getCook
        );
        Map<String, String> replacements = Map.of(
                "-a", "--age",
                "-d", "--distill",
                "-c", "--cook"
        );
        Set<String> mandatory = new HashSet<>(Set.of("--cook"));
        Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
        List<BrewingStep> steps = new ArrayList<>();
        Set<String> used = new HashSet<>();
        while (!arguments.isEmpty()) {
            String operatorName = arguments.poll();
            if (replacements.containsKey(operatorName)) {
                operatorName = replacements.get(operatorName);
            }
            if (used.contains(operatorName)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNIQUE_ARGUMENT, Placeholder.unparsed("argument", operatorName)));
                return false;
            }
            Function<Queue<String>, BrewingStep> operator = operators.get(operatorName);
            if (operator == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNKNOWN_ARGUMENT, Placeholder.unparsed("argument", operatorName)));
                return false;
            }
            steps.add(operator.apply(arguments));
            mandatory.remove(operatorName);
            used.add(operatorName);
        }
        if (!mandatory.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_MISSING_MANDATORY_ARGUMENT, Placeholder.unparsed("arguments", mandatory.toString())));
            return false;
        }
        ItemStack brewItem = BrewAdapter.toItem(new Brew(steps));
        player.getWorld().dropItem(player.getLocation(), brewItem);
        return true;
    }

    private static BrewingStep getCook(Queue<String> arguments) {
        long cookTime = (long) (Double.parseDouble(arguments.poll()) * Moment.MINUTE);
        CauldronType cauldronType = Registry.CAULDRON_TYPE.get(BreweryKey.parse(arguments.poll()));
        List<String> ingredientStrings = new ArrayList<>();
        while (!arguments.isEmpty() && !arguments.peek().startsWith("-")) {
            ingredientStrings.add(arguments.poll());
        }
        Map<Ingredient<ItemStack>, Integer> ingredients = BukkitIngredientManager.INSTANCE.getIngredientsWithAmount(ingredientStrings);
        return new BrewingStep.Cook(new PassedMoment(cookTime), ingredients, cauldronType);
    }

    private static BrewingStep getDistill(Queue<String> arguments) {
        int distillAmount = Integer.parseInt(arguments.poll());
        return new BrewingStep.Distill(distillAmount);
    }

    private static BrewingStep getAge(Queue<String> arguments) {
        long age = (long) (Double.parseDouble(arguments.poll()) * Moment.AGING_YEAR);
        BarrelType barrelType = Registry.BARREL_TYPE.get(BreweryKey.parse(arguments.poll()));
        return new BrewingStep.Age(new PassedMoment(age), barrelType);
    }
}
