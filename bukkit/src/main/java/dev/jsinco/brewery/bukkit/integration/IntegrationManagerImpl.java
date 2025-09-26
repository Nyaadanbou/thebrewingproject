package dev.jsinco.brewery.bukkit.integration;

import dev.jsinco.brewery.bukkit.api.integration.IntegrationTypes;
import dev.jsinco.brewery.bukkit.integration.chest_shop.QuickShopHikariIntegration;
import dev.jsinco.brewery.bukkit.integration.item.*;
import dev.jsinco.brewery.bukkit.integration.placeholder.MiniPlaceholdersIntegration;
import dev.jsinco.brewery.bukkit.integration.placeholder.PlaceholderApiIntegration;
import dev.jsinco.brewery.bukkit.integration.structure.*;
import dev.jsinco.brewery.api.integration.Integration;
import dev.jsinco.brewery.api.integration.IntegrationManager;
import dev.jsinco.brewery.api.integration.IntegrationType;
import dev.jsinco.brewery.api.util.Logger;
import lombok.Getter;

import java.util.Set;

public class IntegrationManagerImpl implements IntegrationManager {
    @Getter
    private final IntegrationRegistry integrationRegistry = new IntegrationRegistry();

    public void registerIntegrations() {
        register(IntegrationTypes.STRUCTURE, new WorldGuardIntegration());
        register(IntegrationTypes.STRUCTURE, new BoltIntegration());
        register(IntegrationTypes.STRUCTURE, new GriefPreventionIntegration());
        register(IntegrationTypes.STRUCTURE, new HuskClaimsIntegration());
        register(IntegrationTypes.STRUCTURE, new LandsIntegration());
        register(IntegrationTypes.STRUCTURE, new TownyIntegration());
        register(IntegrationTypes.ITEM, new CraftEngineIntegration());
        register(IntegrationTypes.ITEM, new ItemsAdderIntegration());
        register(IntegrationTypes.ITEM, new KoishIntegration());
        register(IntegrationTypes.ITEM, new NexoIntegration());
        register(IntegrationTypes.ITEM, new OraxenIntegration());
        register(IntegrationTypes.ITEM, new MmoItemsIntegration());
        register(IntegrationTypes.PLACEHOLDER, new PlaceholderApiIntegration());
        register(IntegrationTypes.PLACEHOLDER, new MiniPlaceholdersIntegration());
        register(IntegrationTypes.CHEST_SHOP, new QuickShopHikariIntegration());
    }

    public void loadIntegrations() {
        integrationRegistry.getIntegrations(IntegrationTypes.ITEM).forEach(Integration::onLoad);
        integrationRegistry.getIntegrations(IntegrationTypes.STRUCTURE).forEach(Integration::onLoad);
        integrationRegistry.getIntegrations(IntegrationTypes.PLACEHOLDER).forEach(Integration::onLoad);
        integrationRegistry.getIntegrations(IntegrationTypes.CHEST_SHOP).forEach(Integration::onLoad);
    }

    public void enableIntegrations() {
        integrationRegistry.getIntegrations(IntegrationTypes.ITEM).forEach(Integration::onEnable);
        integrationRegistry.getIntegrations(IntegrationTypes.STRUCTURE).forEach(Integration::onEnable);
        integrationRegistry.getIntegrations(IntegrationTypes.PLACEHOLDER).forEach(Integration::onEnable);
        integrationRegistry.getIntegrations(IntegrationTypes.CHEST_SHOP).forEach(Integration::onEnable);
    }

    @Override
    public <T extends Integration> void register(IntegrationType<? extends T> type, T integration) {
        if (!integration.isEnabled()) {
            return;
        }

        Logger.log("Registering integration " + integration.getId() + " with type " + type.name());
        integrationRegistry.register(type, integration);
    }

    public void clear() {
        integrationRegistry.clear();
    }

    public <T extends Integration> Set<T> retrieve(IntegrationType<T> type) {
        Set<T> integrations = integrationRegistry.getIntegrations(type);
        if (integrations.isEmpty()) {
            return Set.of();
        }
        return integrations;
    }
}
