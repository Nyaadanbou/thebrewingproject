package dev.jsinco.brewery.bukkit.structure;

import com.google.common.base.Preconditions;
import dev.thorinwasher.schem.Schematic;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;

public class BreweryStructure {

    private final Schematic schem;
    private final List<Vector3i> entryPoints;
    @Getter
    private final String name;

    /**
     * Construct a schem structure where all blocks can finalize the structure (less performant)
     *
     * @param schem
     */
    public BreweryStructure(@NotNull Schematic schem, @NotNull String name) {
        this.schem = Objects.requireNonNull(schem);
        this.entryPoints = computeOrigins(schem);
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Construct a schem structure where only one block can finalize the structure
     *
     * @param schem
     * @param origin
     */
    public BreweryStructure(@NotNull Schematic schem, @NotNull Vector3i origin, @NotNull String name) {
        this.schem = Objects.requireNonNull(schem);
        this.entryPoints = List.of(origin);
        this.name = Objects.requireNonNull(name);
    }

    private static List<Vector3i> computeOrigins(Schematic schem) {
        List<Vector3i> vector3iList = new ArrayList<>();
        schem.apply(new Matrix3d(), (position, blockData) -> {
            if (blockData.getMaterial().isAir()) {
                return;
            }
            vector3iList.add(position);
        });
        return List.copyOf(vector3iList);
    }

    public Optional<Location> findValidOrigin(Matrix3d transformation, Location entryPoint) {
        Preconditions.checkNotNull(entryPoint.getWorld(), "World for entry point can not be null!");
        for (Vector3i structureEntryPoint : entryPoints) {
            Vector3d transformedEntryPoint = transformation.transform(new Vector3d(structureEntryPoint));
            Location worldOrigin = entryPoint.clone().subtract((int) transformedEntryPoint.x(), (int) transformedEntryPoint.y(), (int) transformedEntryPoint.z());
            if (matches(transformation, worldOrigin)) {
                return Optional.of(worldOrigin);
            }
        }
        return Optional.empty();
    }

    private boolean matches(Matrix3d transformation, Location structureWorldOrigin) {
        Map<Location, BlockData> expectedBlocks = getExpectedBlocks(transformation, structureWorldOrigin);
        for (Map.Entry<Location, BlockData> expected : expectedBlocks.entrySet()) {
            World world = expected.getKey().getWorld();
            if (!world.getWorldBorder().isInside(expected.getKey())
                    || world.getMinHeight() > expected.getKey().getBlockY()
                    || world.getMaxHeight() <= expected.getKey().getBlockY()) {
                return false;
            }
            if (!expected.getKey().getBlock().getBlockData().equals(expected.getValue())) {
                return false;
            }
        }
        return true;
    }

    public Map<Location, BlockData> getExpectedBlocks(Matrix3d transformation, Location structureWorldOrigin) {
        Preconditions.checkNotNull(structureWorldOrigin.getWorld(), "World for world origin can not be null!");
        Map<Location, BlockData> output = new HashMap<>();

        schem.apply(transformation, (schematicSpacePosition, blockData) -> {
            if (blockData.getMaterial().isAir()) {
                return;
            }
            output.put(structureWorldOrigin.clone().add(schematicSpacePosition.x(), schematicSpacePosition.y(), schematicSpacePosition.z()), blockData);
        });
        return output;
    }

    public List<BlockData> getPalette() {
        return Arrays.asList(schem.palette());
    }
}
