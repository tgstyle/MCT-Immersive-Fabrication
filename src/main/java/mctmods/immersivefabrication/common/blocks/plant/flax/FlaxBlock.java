package mctmods.immersivefabrication.common.blocks.plant.flax;

import mctmods.immersivefabrication.common.blocks.plant.PlantProperties;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class FlaxBlock extends BushBlock implements BonemealableBlock {
    public static final EnumProperty<FlaxStage> TYPE = EnumProperty.create("type", FlaxStage.class);
    private static final FlaxStage[] STAGES = FlaxStage.values();
    private static final double[] HEIGHTS = {6.0D, 8.0D, 10.0D, 12.0D, 14.0D, 16.0D};
    private static final VoxelShape[] SHAPES = new VoxelShape[HEIGHTS.length];
    private static final int MIN_LIGHT = 9;
    private static final int MAX_LIGHT = 15;
    private static final int BASE_PERIOD = 4;
    private static final int LIGHT_PERIOD = 3;

    static { for (int i = 0; i < SHAPES.length; i++) { SHAPES[i] = Block.box(0.0D, 0.0D, 0.0D, 16.0D, HEIGHTS[i], 16.0D); } }

    public FlaxBlock() {
        super(PlantProperties.crop());
        registerDefaultState(stateDefinition.any().setValue(TYPE, FlaxStage.STAGE0));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) { builder.add(TYPE); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPES[state.getValue(TYPE).ordinal()]; }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return state.is(Blocks.FARMLAND); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public ItemStack getCloneItemStack(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state) { return new ItemStack(IFabItems.SEEDS.get()); }

    @SuppressWarnings("deprecation")
    @Override public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        int stage = state.getValue(TYPE).ordinal();
        if (stage >= FlaxStage.RIPE_STAGE) { return; }
        int light = level.getRawBrightness(pos.above(), 0);
        if (light < MIN_LIGHT) { return; }
        int period = BASE_PERIOD + (MAX_LIGHT - light) * LIGHT_PERIOD;
        if (isFertile(level.getBlockState(pos.below()))) { period = Math.max(1, period / 2); }
        if (random.nextInt(period) == 0) { setStage(level, pos, stage + 1); }
    }

    private static boolean isFertile(BlockState below) { return below.is(Blocks.FARMLAND) && below.getValue(FarmBlock.MOISTURE) > 0; }

    private void setStage(Level level, BlockPos pos, int stage) { level.setBlock(pos, defaultBlockState().setValue(TYPE, STAGES[Math.min(stage, FlaxStage.RIPE_STAGE)]), Block.UPDATE_ALL); }

    @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) { return state.getValue(TYPE) != FlaxStage.STAGE5; }

    @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

    @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int stage = state.getValue(TYPE).ordinal();
        setStage(level, pos, stage + 1 + random.nextInt(Math.max(1, FlaxStage.RIPE_STAGE - stage)));
    }
}
