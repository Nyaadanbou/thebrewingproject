package dev.jsinco.brewery.breweries;

public interface Distillery<D extends Distillery<D, IS, I>, IS, I> extends StructureHolder<D>, InventoryAccessible<IS, I> {

    long getStartTime();

    void tick();
}
