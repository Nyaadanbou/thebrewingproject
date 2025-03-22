package dev.jsinco.brewery.breweries;

public interface Distillery<D extends Distillery<D, I>, I> extends StructureHolder<D>, InventoryAccessible<I> {

    long getStartTime();

    void tick();
}
