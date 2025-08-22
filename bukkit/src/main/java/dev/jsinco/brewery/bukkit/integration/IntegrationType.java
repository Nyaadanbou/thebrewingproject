package dev.jsinco.brewery.bukkit.integration;

public record IntegrationType<T extends Integration>(Class<T> integrationClass, String name) {
    public static IntegrationType<ItemIntegration> ITEM = new IntegrationType<>(ItemIntegration.class, "item integration");
    public static IntegrationType<StructureIntegration> STRUCTURE = new IntegrationType<>(StructureIntegration.class, "structure integration");
    public static IntegrationType<PlaceholderIntegration> PLACEHOLDER = new IntegrationType<>(PlaceholderIntegration.class, "placeholder integration");
}
