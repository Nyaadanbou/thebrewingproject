package dev.jsinco.brewery.bukkit.structure;

import dev.jsinco.brewery.util.Util;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class StructureReaderTest {

    @MockBukkitInject
    ServerMock serverMock;
    WorldMock worldMock;

    @BeforeEach
    void setUp() {
        this.worldMock = serverMock.addSimpleWorld("test_world");
    }

    @ParameterizedTest
    @MethodSource("getSchemFormatPaths")
    void fromJson_names(String path) throws StructureReadException, IOException {
        String structureName = path.replace("/structures/", "").replace(".json", "");
        Map<String, BreweryStructure> structures = StructureReader.fromInternalResourceJson(path);
        assertTrue(structures.containsKey(structureName + "$oak"));
    }

    @ParameterizedTest
    @MethodSource("getSchemFormatPaths")
    void fromJson_hasAllMaterials(String path) throws StructureReadException, IOException {
        Map<String, BreweryStructure> structures = StructureReader.fromInternalResourceJson(path);
        Set<BlockData> uniqueBlockData = new HashSet<>();
        for (BreweryStructure breweryStructure : structures.values()) {
            List<BlockData> palette = breweryStructure.getPalette();
            for (BlockData blockData : palette) {
                if (blockData.getMaterial().isAir()) {
                    continue;
                }
                assertFalse(uniqueBlockData.contains(blockData), "Duplicate entry of: " + blockData.getAsString());
                uniqueBlockData.add(blockData);
            }
        }
    }

    static Stream<Arguments> getSchemFormatPaths() {
        return Stream.of("/structures/large_barrel.json", "/structures/small_barrel.json").map(Arguments::of);
    }
}