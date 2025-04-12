package dev.jsinco.brewery.bukkit.command;

import lombok.Getter;

public enum SubCommand {
    CREATE("brewery.command.create"),
    EVENT("brewery.command.event"),
    STATUS("brewery.command.status"),
    INFO("brewery.command.info"),
    RELOAD("brewery.command.reload"),
    SEAL("brewery.command.seal");

    @Getter
    private final String permissionNode;

    SubCommand(String permissionNode) {
        this.permissionNode = permissionNode;
    }
}
