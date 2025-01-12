package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.breweries.BehaviorHolder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class PlacedBreweryStructure {
    private static final List<Matrix3d> ALLOWED_TRANSFORMATIONS = compileAllowedTransformations();
    private final BreweryStructure structure;
    private final Matrix3d transformation;
    private final Location worldOrigin;
    @Setter
    private @Nullable BehaviorHolder holder = null;

    public PlacedBreweryStructure(BreweryStructure structure, Matrix3d transformation,
                                  Location worldOrigin) {
        this.structure = structure;
        this.transformation = transformation;
        this.worldOrigin = worldOrigin;
    }

    public static Optional<PlacedBreweryStructure> findValid(BreweryStructure structure, Location worldOrigin) {
        for (Matrix3d transformation : ALLOWED_TRANSFORMATIONS) {
            Optional<Location> possibleOrigin = structure.findValidOrigin(transformation, worldOrigin);
            if (possibleOrigin.isPresent()) {
                return possibleOrigin
                        .map(origin -> new PlacedBreweryStructure(structure, transformation, origin));
            }
        }
        return Optional.empty();
    }

    public List<Location> getPositions() {
        return List.copyOf(structure.getExpectedBlocks(transformation, worldOrigin)
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
