package dev.jsinco.brewery.neoforge;

import dev.jsinco.brewery.neoforge.listeners.BlockEventHandler;
import dev.jsinco.brewery.neoforge.listeners.PlayerEventHandler;
import dev.jsinco.brewery.neoforge.listeners.WorldEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.io.IOException;

@Mod("the_brewing_project")
public class TheBrewingProject {

    public TheBrewingProject(IEventBus modBus, ModContainer container) {
        modBus.register(new BlockEventHandler());
        modBus.register(new PlayerEventHandler());
        modBus.register(new WorldEventHandler());
        modBus.addListener(this::serverStart);
    }

    private void serverStart(ServerStartingEvent event) {
        try(ServerLevel level = event.getServer().overworld()) {
            level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(BarrelStorage::create, BarrelStorage::load), "barrel");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
