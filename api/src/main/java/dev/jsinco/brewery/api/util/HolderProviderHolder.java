package dev.jsinco.brewery.api.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class HolderProviderHolder {

    private static HolderProvider instance;

    @ApiStatus.Internal
    public static HolderProvider instance() {
        if (instance == null) {
            instance = ServiceLoader.load(HolderProvider.class, HolderProvider.class.getClassLoader()).findFirst()
                    .orElseThrow();
        }
        return instance;
    }
}
