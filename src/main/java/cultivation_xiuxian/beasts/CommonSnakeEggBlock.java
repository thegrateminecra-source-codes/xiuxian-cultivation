package cultivation_xiuxian.beasts;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CommonSnakeEggBlock extends Block {
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 4.0, 9.0, 7.0, 8.0);
    private static final float ADVANCE_CHANCE = 0.30F;

    public CommonSnakeEggBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() >= ADVANCE_CHANCE) {
            return;
        }
        int hatch = state.getValue(HATCH);
        if (hatch < 2) {
            level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.setBlock(pos, state.setValue(HATCH, hatch + 1), 2);
        } else {
            level.playSound(null, pos, CommonSounds.SNAKE_HURT, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.removeBlock(pos, false);
            CommonSnakeBeast snake = CommonBeasts.COMMON_SNAKE_BEAST.create(level, EntitySpawnReason.BREEDING);
            if (snake != null) {
                snake.setAge(-24000); // hatches as a baby
                snake.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
                level.addFreshEntity(snake);
            }
        }
    }
}
