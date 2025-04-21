package dev.jsinco.brewery.bukkit.command;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.bukkit.brew.BrewAdapter;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Optional;

public class SealCommand {

    public static boolean onCommand(Player target, CommandSender sender, String[] args) {
        PlayerInventory targetInventory = target.getInventory();
        boolean sealAll = args.length > 1 && args[1].equals("all");
        if (sealAll) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }
        Component volumeMessage = args.length > 1 ? LegacyComponentSerializer.legacyAmpersand().deserialize(
                String.join(" ", Arrays.copyOfRange(args, 1, args.length))
        ) : null;
        String serializedVolumeMessage = volumeMessage != null ? MiniMessage.miniMessage().serialize(volumeMessage) : null;
        if (!sealAll) {
            BrewAdapter.fromItem(targetInventory.getItemInMainHand())
                    .map(brew -> BrewAdapter.toItem(brew, new Brew.State.Seal(serializedVolumeMessage)))
                    .ifPresentOrElse(itemStack -> {
                        targetInventory.setItemInMainHand(itemStack);
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_SEAL_SUCCESS, Placeholder.unparsed("player_name", target.getName())));
                    }, () -> {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_SEAL_FAILURE));
                    });
        } else {
            boolean oneFound = false;
            for (int i = 0; i < targetInventory.getSize(); i++) {
                ItemStack itemStack = targetInventory.getItem(i);
                if (itemStack == null) {
                    continue;
                }
                Optional<Brew> brewOptional = BrewAdapter.fromItem(itemStack);
                if (brewOptional.isPresent()) {
                    oneFound = true;
                    targetInventory.setItem(i, BrewAdapter.toItem(brewOptional.get(), new Brew.State.Seal(serializedVolumeMessage)));
                }
            }
            if (oneFound) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_SEAL_SUCCESS, Placeholder.unparsed("player_name", target.getName())));
            } else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.COMMAND_SEAL_FAILURE));
            }
        }
        return true;
    }
}
