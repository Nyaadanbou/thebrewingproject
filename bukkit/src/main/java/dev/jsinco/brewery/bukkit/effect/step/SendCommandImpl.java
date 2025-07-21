package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.event.step.SendCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SendCommandImpl extends SendCommand implements ExecutableEventStep {
    
    public SendCommandImpl(String command, CommandSenderType senderType) {
        super(command, senderType);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        switch (getSenderType()) {
            case PLAYER -> player.performCommand(getCommand());
            case SERVER -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), getCommand());
        }
    }

}
