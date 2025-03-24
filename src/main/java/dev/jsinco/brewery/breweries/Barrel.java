package dev.jsinco.brewery.breweries;

public interface Barrel<B extends Barrel<B, IS, I>, IS, I> extends StructureHolder<B>, InventoryAccessible<IS, I> {

    void destroy();

    void tick();
}
