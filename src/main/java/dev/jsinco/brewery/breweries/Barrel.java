package dev.jsinco.brewery.breweries;

public interface Barrel<B extends Barrel<B, I>, I> extends StructureHolder<B>, InventoryAccessible<I> {

    void destroy();

    void tick();
}
