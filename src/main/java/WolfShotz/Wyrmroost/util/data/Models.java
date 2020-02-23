package WolfShotz.Wyrmroost.util.data;

import WolfShotz.Wyrmroost.Wyrmroost;
import WolfShotz.Wyrmroost.content.blocks.CanariLeavesBlock;
import WolfShotz.Wyrmroost.content.items.CustomSpawnEggItem;
import WolfShotz.Wyrmroost.registry.WRBlocks;
import WolfShotz.Wyrmroost.registry.WRItems;
import WolfShotz.Wyrmroost.util.ModUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;

public class Models
{
    public static class BlockModels extends BlockStateProvider
    {
        public final ExistingFileHelper theGOODexistingFileHelper;

        public BlockModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
        {
            super(generator, Wyrmroost.MOD_ID, existingFileHelper);
            this.theGOODexistingFileHelper = existingFileHelper;
        }

        @Override
        protected void registerStatesAndModels()
        {
            logBlock((LogBlock) WRBlocks.ASH_LOG.get());

            logBlock((LogBlock) WRBlocks.CANARI_LOG.get());
            axisBlock((RotatedPillarBlock) WRBlocks.STRIPPED_CANARI.get());
            simpleBlock(WRBlocks.CANARI_WOOD.get(), "canari_log"); // The all sided bark blocks. its called "wood" in vanilla so idgaf

            ModelFile leaves = getExistingFile(mcLoc(BLOCK_FOLDER + "/leaves"));
            getVariantBuilder(WRBlocks.CANARI_LEAVES.get()) // This took a stupid amount of time to figure out please help me
                    .partialState()
                    .with(CanariLeavesBlock.BERRIES, true)
                    .modelForState().modelFile(cubeAll("canari_leaves_berries", modLoc("block/canari_leaves_berries")).parent(leaves)).addModel()
                    .partialState()
                    .with(CanariLeavesBlock.BERRIES, false)
                    .modelForState().modelFile(cubed(WRBlocks.CANARI_LEAVES.get()).parent(leaves)).addModel();

            logBlock((LogBlock) WRBlocks.BLUE_CORIN_LOG.get());
            axisBlock((RotatedPillarBlock) WRBlocks.STRIPPED_BLUE_CORIN_LOG.get());
            simpleBlock(WRBlocks.BLUE_CORIN_WOOD.get(), "blue_corin_log");

            logBlock((LogBlock) WRBlocks.TEAL_CORIN_LOG.get());
            axisBlock((RotatedPillarBlock) WRBlocks.STRIPPED_TEAL_CORIN_LOG.get());
            simpleBlock(WRBlocks.TEAL_CORIN_WOOD.get(), "teal_corin_log");

            logBlock((LogBlock) WRBlocks.RED_CORIN_LOG.get());
            simpleBlock(WRBlocks.RED_CORIN_WOOD.get(), "red_corin_log");
            axisBlock((RotatedPillarBlock) WRBlocks.STRIPPED_RED_CORIN_LOG.get());

            // All unregistered blocks will be done here. They will be simple blocks with all sides of the same texture
            // If this is unwanted, it is important to define so above
            for (Block block : ModUtils.getRegistryEntries(WRBlocks.BLOCKS))
            {
                if (registeredBlocks.containsKey(block)) continue;

                ResourceLocation name = block.getRegistryName();
                if (!theGOODexistingFileHelper.exists(new ResourceLocation(name.getNamespace(), BLOCK_FOLDER + "/" + name.getPath()), ResourcePackType.CLIENT_RESOURCES, ".png", "textures"))
                    ModUtils.L.error("Texture {} does not exist in any resource pack! Skipping this model...", name);
                else simpleBlock(block);
            }

        }

        protected void axisBlock(RotatedPillarBlock block)
        {
            axisBlock(block, blockTexture(block), new ResourceLocation(blockTexture(block) + "_top"));
        }

        protected void simpleBlock(Block block, String path)
        {
            simpleBlock(block, cubeAll(block.getRegistryName().getPath(), ModUtils.resource("block/" + path)));
        }

        public BlockModelBuilder cubed(Block block)
        {
            return cubeAll(block.getRegistryName().getPath(), blockTexture(block));
        }
    }

    public static class ItemModels extends ItemModelProvider
    {
        final ExistingFileHelper theGOODExistingFileHelper;
        private final List<Item> REGISTERED = Lists.newArrayList();

        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
        {
            super(generator, Wyrmroost.MOD_ID, existingFileHelper);
            this.theGOODExistingFileHelper = existingFileHelper;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void registerModels()
        {
            // SpawnEggs
            for (CustomSpawnEggItem item : CustomSpawnEggItem.EGG_TYPES)
                itemBare(item).parent(new ModelFile.UncheckedModelFile(mcLoc("item/template_spawn_egg")));

            // Dragon Egg
            item(WRItems.DRAGON_EGG.get()) // TODO make this a custom baked model...
                    .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                    .transforms()
                        .transform(ModelBuilder.Perspective.GUI).rotation(160, 8, 30).translation(21, 6, 0).scale(1.5f).end()
                        .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT).rotation(180, -35, 0).translation(2, 11, -12).end()
                        .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT).rotation(180, 35, 0).translation(-2, 11, -12).end()
                        .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT).rotation(253, 65, 0).translation(8, 2, 10).scale(0.75f).end()
                        .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT).rotation(253, 65, 0).translation(3, 13, 7).scale(0.75f).end()
                        .transform(ModelBuilder.Perspective.GROUND).rotation(180, 0, 0).translation(4, 8, -5).scale(0.55f);

            // Minutus
            getBuilder("minutus_alive")
                    .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                    .texture("layer0", resource("minutus_alive"));
            item(WRItems.MINUTUS.get())
                    .override()
                    .predicate(ModUtils.resource("isalive"), 1)
                    .model(new ModelFile.UncheckedModelFile(resource("minutus_alive")));

            // Dragon Staff
            item(WRItems.DRAGON_STAFF.get()).parent(new ModelFile.UncheckedModelFile("item/handheld"));

            // Item Blocks
            item(WRBlocks.CINIS_ROOT.get().asItem());
            for (RegistryObject<Block> registryBlock : WRBlocks.BLOCKS.getEntries()) // All Standard ItemBlocks
            {
                if (REGISTERED.contains(registryBlock.get().asItem())) continue;
                ResourceLocation path = registryBlock.get().getRegistryName();
                ModelFile model = new ModelFile.UncheckedModelFile(path.getNamespace() + ":block/" + path.getPath());
                itemBare(registryBlock.get().asItem()).parent(model);
            }

            // All items that do not require custom attention
            ModUtils.getRegistryEntries(WRItems.ITEMS)
                    .stream()
                    .filter(e -> !REGISTERED.contains(e))
                    .forEach(this::item);
        }

        public ItemModelBuilder item(Item item)
        {
            ItemModelBuilder builder = itemBare(item);

            // model
            String parent = (item instanceof TieredItem) ? "item/handheld" : "item/generated";
            builder.parent(new ModelFile.UncheckedModelFile(parent));

            // texture
            ResourceLocation texture = resource(item.getRegistryName().getPath());
            if (!theGOODExistingFileHelper.exists(texture, ResourcePackType.CLIENT_RESOURCES, ".png", "textures"))
            {
                ModUtils.L.error("Texture {} does not exist in any resource pack! Skipping...", texture);
            }
            else builder.texture("layer0", texture);

            return builder;
        }

        public ItemModelBuilder itemBare(Item item)
        {
            REGISTERED.add(item);
            return getBuilder(item.getRegistryName().getPath());
        }

        private static ResourceLocation resource(String path)
        {
            return ModUtils.resource("item/" + path);
        }

        @Override
        public String getName() { return "Wyrmroost Item Models"; }
    }
}
