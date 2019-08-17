package WolfShotz.Wyrmroost.event;

import WolfShotz.Wyrmroost.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SetupOreGen
{
    private static final Set<Biome> overworldOnly = new HashSet<>(ModUtils.collectAll(BiomeDictionary.getBiomes(BiomeDictionary.Type.END), BiomeDictionary.getBiomes(BiomeDictionary.Type.NETHER)));
    private static final Predicate<Biome> overworldFilter = biome -> overworldOnly.stream().noneMatch(biome::equals);
    
    private static CountRangeConfig platinumConfig = new CountRangeConfig(2, 0, 0, 28);
    private static CountRangeConfig geodeConfig = new CountRangeConfig(1, 0, 0, 16);

    public static void setupOreGen() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (overworldFilter.test(biome)) { // Filter Ores so they dont gen in the nether or end
                registerOreEntry(biome, SetupBlocks.blockgeodeore.getDefaultState(), 8, geodeConfig);
                registerOreEntry(biome, SetupBlocks.blockplatinumore.getDefaultState(), 9, platinumConfig);
                ModUtils.L.debug("Registering Ore for:" + biome);
            }
        }
    }

    /**
     * Helper method that turns this rediculously long line into something more convenient and readable...
     * Takes in the biome, ore blockstate, ore size and the chance configuration as params.
     */
    private static void registerOreEntry(Biome biome, BlockState state, int size, CountRangeConfig config) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, state, size), Placement.COUNT_RANGE, config));
    }
}
