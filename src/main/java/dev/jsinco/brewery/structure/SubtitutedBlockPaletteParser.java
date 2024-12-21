package dev.jsinco.brewery.structure;

import dev.thorinwasher.schem.blockpalette.BlockPaletteParser;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.util.Set;

public class SubtitutedBlockPaletteParser implements BlockPaletteParser {

    private final Set<String> patterns;
    private final String target;

    public SubtitutedBlockPaletteParser(Set<String> patterns, String target) {
        this.patterns = patterns;
        this.target = target;
    }

    @Override
    public BlockData parse(String materialsString) {
        String compiledMaterialsString = materialsString;
        for (String pattern : patterns) {
            compiledMaterialsString = compiledMaterialsString.replace(pattern, target);
        }
        return Bukkit.createBlockData(compiledMaterialsString);
    }
}
