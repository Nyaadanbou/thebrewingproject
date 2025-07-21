package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.SendCommand.CommandSenderType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SendCommandExecutable implements ExecutableEventStep {

    private final String command;
    private final CommandSenderType senderType;
    
    public SendCommandExecutable(String command, CommandSenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        switch (senderType) {
            case PLAYER -> player.performCommand(command);
            case SERVER -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

}
