package dev.jsinco.brewery.structure;

import com.google.common.base.Preconditions;
import dev.thorinwasher.schem.Schematic;
import dev.thorinwasher.schem.SchematicReader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreweryStructure {

    private final Schematic schem;
    private final Vector3i origin;

    public BreweryStructure(Schematic schem, Vector3i origin) {
        this.schem = schem;
        this.origin = origin;
    }

    public BreweryStructure(InputStream inputStream, Vector3i origin) {
        this(new SchematicReader().read(inputStream), origin);
    }

    public boolean isValid(Matrix3d transformation, Location structureWorldOrigin) {
        Preconditions.checkNotNull(structureWorldOrigin.getWorld(), "World for world origin can not be null!");
        Map<Location, BlockData> expectedBlocks = getExpectedBlocks(transformation, structureWorldOrigin);
        for (Map.Entry<Location, BlockData> expected : expectedBlocks.entrySet()) {
            if (expected.getKey().getBlock().getBlockData().equals(expected.getValue())) {
                return false;
            }
        }
        return true;
    }

    public Vector3d transform(Matrix3d transformation, Location structureWorldOrigin, Vector3i schematicSpacePosition) {
        Vector3d transformedOrigin = transformation.transform(new Vector3d(this.origin));
        Vector3i vector = schematicSpacePosition.sub(new Vector3i((int) transformedOrigin.x(), (int) transformedOrigin.y(), (int) transformedOrigin.z()), new Vector3i());
        return transformation.transform(new Vector3d(vector)).add(structureWorldOrigin.getX(), structureWorldOrigin.getY(), structureWorldOrigin.getZ());
    }

    public Map<Location, BlockData> getExpectedBlocks(Matrix3d transformation, Location structureWorldOrigin) {
        Preconditions.checkNotNull(structureWorldOrigin.getWorld(), "World for world origin can not be null!");
        Map<Location, BlockData> output = new HashMap<>();
        World world = structureWorldOrigin.getWorld();
        schem.apply(transformation, (schematicSpacePosition, blockData) -> {
            Vector3d worldCoordinates = transform(transformation, structureWorldOrigin, schematicSpacePosition);
            output.put(new Location(world, worldCoordinates.x, worldCoordinates.y, worldCoordinates.z), blockData);
        });
        return output;
    }

    public List<BlockData> getPalette() {
        return Arrays.asList(schem.palette());
    }
}
