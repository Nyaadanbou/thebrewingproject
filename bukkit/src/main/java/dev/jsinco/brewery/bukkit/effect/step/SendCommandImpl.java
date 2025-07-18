package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.step.SendCommand;
import dev.jsinco.brewery.util.Holder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class SendCommandImpl extends SendCommand {
    
    public SendCommandImpl(String command, CommandSenderType senderType) {
        super(command, senderType);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer.value());
        if (player == null) {
            return;
        }

        switch (getSenderType()) {
            case PLAYER -> player.performCommand(getCommand());
            case SERVER -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), getCommand());
        }
    }
}
