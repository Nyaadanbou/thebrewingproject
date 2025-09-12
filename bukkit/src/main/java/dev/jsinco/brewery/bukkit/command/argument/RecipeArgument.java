package dev.jsinco.brewery.bukkit.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jsinco.brewery.api.recipe.Recipe;
import dev.jsinco.brewery.api.recipe.RecipeRegistry;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.util.BukkitMessageUtil;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class RecipeArgument implements CustomArgumentType.Converted<Recipe<ItemStack>, String> {
    private static final DynamicCommandExceptionType ERROR_INVALID_RECIPE = new DynamicCommandExceptionType(event ->
            BukkitMessageUtil.toBrigadier("tbp.command.illegal-argument-detailed", Placeholder.unparsed("argument", event.toString()))
    );

    private static final Pattern WORD_ARGUMENT = Pattern.compile("[a-zA-Z0-9+\\-_.]+");

    @Override
    public Recipe<ItemStack> convert(String nativeType) throws CommandSyntaxException {
        RecipeRegistry<ItemStack> registry = TheBrewingProject.getInstance().getRecipeRegistry();
        return registry.getRecipe(nativeType)
                .orElseThrow(() -> ERROR_INVALID_RECIPE.create(nativeType));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
        TheBrewingProject.getInstance().getRecipeRegistry()
                .getRecipes()
                .stream()
                .map(Recipe::getRecipeName)
                .filter(recipeName -> recipeName.startsWith(builder.getRemainingLowerCase()))
                .map(this::sanitizeName)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private String sanitizeName(String name) {
        if (WORD_ARGUMENT.matcher(name).matches()) {
            return name;
        }
        return "\"" + name + "\"";
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
