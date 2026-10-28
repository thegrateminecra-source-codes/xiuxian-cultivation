package cultivation_xiuxian.blocks;

import cultivation_xiuxian.CultivationXiuxian;
import cultivation_xiuxian.beasts.CommonSnakeEggBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CultivationBlocks {
    // this bit Registers all of my Custom blocks - i have not made any yet - the egg is teqnically a block but players cant get hands on it.
    public static final Block COMMON_SNAKE_EGG = register("common_snake_egg",
            properties -> properties
                    .mapColor(MapColor.GRASS)
                    .forceSolidOn()
                    .strength(0.5F)
                    .sound(SoundType.CALCITE)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.POPPED),
            CommonSnakeEggBlock::new);

    private static Block register(String name,
                                  java.util.function.Function<BlockBehaviour.Properties, BlockBehaviour.Properties> props,
                                  java.util.function.Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> key = ResourceKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath(CultivationXiuxian.MOD_ID, name));
        Block block = factory.apply(props.apply(BlockBehaviour.Properties.of().strength(0.5F).setId(key)));
        Registry.register(BuiltInRegistries.BLOCK, key, block);
        return block;
    }

    public static void init() {
        if (COMMON_SNAKE_EGG.defaultBlockState().is(Blocks.AIR)) {
            throw new IllegalStateException("common_snake_egg registered as air");
        }
    }
}
