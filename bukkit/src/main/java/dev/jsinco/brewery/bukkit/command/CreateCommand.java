package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class CreateCommand {

    private static final List<String> TAB_COMPLETIONS = List.of("-a", "--age", "-d", "--distill", "-c", "--cook");
    private static final Map<String, String> REPLACEMENTS = Map.of(
            "-a", "--age",
            "-d", "--distill",
            "-c", "--cook"
    );

    public static boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission("brewery.command.create")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_NOT_ENOUGH_PERMISSIONS));
            return true;
        }
        Map<String, BiFunction<Queue<String>, CommandSender, BrewingStep>> operators = Map.of(
                "--age", CreateCommand::getAge,
                "--distill", CreateCommand::getDistill,
                "--cook", CreateCommand::getCook
        );

        Set<String> mandatory = new HashSet<>(Set.of("--cook"));
        Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
        List<BrewingStep> steps = new ArrayList<>();
        while (!arguments.isEmpty()) {
            String operatorName = arguments.poll();
            if (REPLACEMENTS.containsKey(operatorName)) {
                operatorName = REPLACEMENTS.get(operatorName);
            }
            BiFunction<Queue<String>, CommandSender, BrewingStep> operator = operators.get(operatorName);
            if (operator == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNKNOWN_ARGUMENT, Placeholder.unparsed("argument", operatorName)));
                return false;
            }
            steps.add(operator.apply(arguments, player));
            mandatory.remove(operatorName);
        }
        if (!mandatory.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_MISSING_MANDATORY_ARGUMENT, Placeholder.unparsed("arguments", mandatory.toString())));
            return false;
        }
        ItemStack brewItem = BrewAdapter.toItem(new Brew(steps));
        player.getWorld().dropItem(player.getLocation(), brewItem);
        return true;
    }

    private static BrewingStep getCook(Queue<String> arguments, CommandSender sender) {
        long cookTime = (long) (Double.parseDouble(arguments.poll()) * Moment.MINUTE);
        CauldronType cauldronType = Registry.CAULDRON_TYPE.get(BreweryKey.parse(arguments.poll()));
        List<String> ingredientStrings = new ArrayList<>();
        while (!arguments.isEmpty() && !arguments.peek().startsWith("-")) {
            ingredientStrings.add(arguments.poll());
        }
        List<String> invalidIngredientArguments = ingredientStrings.stream()
                .filter(ingredient -> !BukkitIngredientManager.INSTANCE.isValidIngredient(ingredient))
                .toList();
        if (!invalidIngredientArguments.isEmpty()) {
            String invalidIngredients = String.join(",", invalidIngredientArguments);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_CREATE_UNKNOWN_ARGUMENT, Placeholder.unparsed("argument", invalidIngredients)));
            throw new IllegalArgumentException("Could not find the ingredient(s): " + invalidIngredients);
        }
        Map<Ingredient<ItemStack>, Integer> ingredients = BukkitIngredientManager.INSTANCE.getIngredientsWithAmount(ingredientStrings);
        return new BrewingStep.Cook(new PassedMoment(cookTime), ingredients, cauldronType);
    }

    private static BrewingStep getDistill(Queue<String> arguments, CommandSender sender) {
        int distillAmount = Integer.parseInt(arguments.poll());
        return new BrewingStep.Distill(distillAmount);
    }

    private static BrewingStep getAge(Queue<String> arguments, CommandSender sender) {
        long age = (long) (Double.parseDouble(arguments.poll()) * Moment.AGING_YEAR);
        BarrelType barrelType = Registry.BARREL_TYPE.get(BreweryKey.parse(arguments.poll()));
        return new BrewingStep.Age(new PassedMoment(age), barrelType);
    }

    public static @Nullable List<String> tabComplete(@NotNull String @NotNull [] args) {
        for (int i = args.length - 2; i >= 0; i--) {
            if (TAB_COMPLETIONS.contains(args[i])) {
                String[] precedingArgs = Arrays.copyOfRange(args, i + 1, args.length);
                return switch (REPLACEMENTS.getOrDefault(args[i], args[i])) {
                    case "--age" -> {
                        if (precedingArgs.length == 1) {
                            yield List.of("<number>");
                        } else if (precedingArgs.length == 2) {
                            yield Registry.BARREL_TYPE.values().stream()
                                    .map(BarrelType::key)
                                    .map(BreweryKey::key)
                                    .filter(barrelType -> barrelType.startsWith(precedingArgs[1]))
                                    .toList();
                        } else if (precedingArgs.length == 3) {
                            yield TAB_COMPLETIONS.stream()
                                    .filter(tabCompletion -> tabCompletion.startsWith(precedingArgs[2]))
                                    .toList();
                        }
                        yield List.of();
                    }
                    case "--cook" -> {
                        if (precedingArgs.length == 1) {
                            yield List.of("<number>");
                        } else if (precedingArgs.length == 2) {
                            yield Registry.CAULDRON_TYPE.values().stream()
                                    .map(CauldronType::key)
                                    .map(BreweryKey::key)
                                    .filter(cauldronType -> cauldronType.startsWith(precedingArgs[1]))
                                    .toList();
                        }
                        yield Stream.concat(
                                Stream.of("<ingredient/amount>"),
                                TAB_COMPLETIONS.stream()
                                        .filter(tabCompletion -> tabCompletion.startsWith(precedingArgs[precedingArgs.length - 1]))
                        ).toList();
                    }
                    case "--distill" -> {
                        if (precedingArgs.length == 1) {
                            yield List.of("<integer>");
                        } else if (precedingArgs.length == 2) {
                            yield TAB_COMPLETIONS.stream()
                                    .filter(tabCompletion -> tabCompletion.startsWith(precedingArgs[1]))
                                    .toList();
                        }
                        yield List.of();
                    }
                    default ->
                            throw new IllegalStateException("Unexpected value: " + REPLACEMENTS.getOrDefault(args[i], args[i]));
                };
            } else if (args[i].startsWith("-")) {
                return List.of();
            }
        }
        return TAB_COMPLETIONS;
    }
}
