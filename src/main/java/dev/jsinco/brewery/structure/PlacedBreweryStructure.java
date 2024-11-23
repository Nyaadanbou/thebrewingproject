package dev.jsinco.brewery.structure;

import org.bukkit.Location;
import org.joml.Matrix3d;

import java.util.List;

public record PlacedBreweryStructure<T>(BreweryStructure structure, Matrix3d transformation,
                                     Location structureOrigin, T holder) {

    public boolean isValid() {
        return structure.isValid(transformation, structureOrigin);
    }

    public List<Location> getPositions() {
        return List.copyOf(structure.getExpectedBlocks(transformation, structureOrigin)
                .keySet());
    }
}
