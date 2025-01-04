package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.objects.Destroyable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlacedBreweryStructure {
    private static final List<Matrix3d> ALLOWED_TRANSFORMATIONS = compileAllowedTransformations();
    private final BreweryStructure structure;
    private final Matrix3d transformation;
    private final Location worldOrigin;
    private final Vector3i structureOrigin;
    @Setter
    @Getter
    private @Nullable Destroyable holder;

    public PlacedBreweryStructure(BreweryStructure structure, Matrix3d transformation,
                                  Location worldOrigin, Vector3i structureOrigin) {
        this.structure = structure;
        this.transformation = transformation;
        this.worldOrigin = worldOrigin;
        this.structureOrigin = structureOrigin;
    }

    public static Optional<PlacedBreweryStructure> findValid(BreweryStructure structure, Location worldOrigin) {
        for (Matrix3d transformation : ALLOWED_TRANSFORMATIONS) {
            Optional<Vector3i> possibleOrigin = structure.findValidOrigin(transformation, worldOrigin);
            if (possibleOrigin.isPresent()) {
                return possibleOrigin.map(origin -> new PlacedBreweryStructure(structure, transformation, worldOrigin, origin));
            }
        }
        return Optional.empty();
    }

    public List<Location> getPositions() {
        return List.copyOf(structure.getExpectedBlocks(transformation, worldOrigin, structureOrigin)
                .keySet());
    }

    private static List<Matrix3d> compileAllowedTransformations() {
        List<Matrix3d> output = new ArrayList<>();
        Matrix3d transformation = new Matrix3d();
        for (int i = 0; i < 4; i++) {
            output.add(transformation.rotate(Math.PI / 2 * i, 0, 1, 0, new Matrix3d()));
        }
        transformation.reflect(1, 0, 0);
        for (int i = 0; i < 4; i++) {
            output.add(transformation.rotate(Math.PI / 2 * i, 0, 1, 0, new Matrix3d()));
        }
        return List.copyOf(output);
    }
}
