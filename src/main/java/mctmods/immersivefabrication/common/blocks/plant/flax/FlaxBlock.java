package mctmods.immersivefabrication.common.blocks.plant.flax;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.util.IFabItemNames;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;

public class FlaxBlock extends BlockBush implements IGrowable {
    public static final PropertyEnum<FlaxStage> TYPE = PropertyEnum.create("type", FlaxStage.class);
    private static final FlaxStage[] STAGES = FlaxStage.values();
    private static final int RIPE_STAGE = FlaxStage.RIPE.ordinal();
    private static final double[] HEIGHTS = {0.375D, 0.5D, 0.625D, 0.75D, 0.875D, 1.0D};
    private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[HEIGHTS.length];
    private static final int MIN_LIGHT = 9;
    private static final int MAX_LIGHT = 15;
    private static final int BASE_PERIOD = 4;
    private static final int LIGHT_PERIOD = 3;
    private static final int FIBER_META = Arrays.asList(IFabItemNames.MATERIALS).indexOf("flax_fiber");
    private static final Random DROP_RANDOM = new Random();

    static { for (int i = 0; i < BOXES.length; i++) { BOXES[i] = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, HEIGHTS[i], 1.0D); } }

    public FlaxBlock() {
        super(Material.PLANTS);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, FlaxStage.STAGE0));
        setRegistryName(ImmersiveFabrication.MODID, "flax");
        setTranslationKey(ImmersiveFabrication.MODID + ".flax");
        setTickRandomly(true);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        disableStats();
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }

    @Override public int getMetaFromState(@Nonnull IBlockState state) { return state.getValue(TYPE).ordinal(); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(TYPE, STAGES[meta >= 0 && meta < STAGES.length ? meta : 0]); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return BOXES[state.getValue(TYPE).ordinal()]; }

    @Override protected boolean canSustainBush(@Nonnull IBlockState state) { return state.getBlock() == Blocks.FARMLAND; }

    @Override @Nonnull public EnumPlantType getPlantType(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return EnumPlantType.Crop; }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return new ItemStack(IFabItems.seed); }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        int age = state.getValue(TYPE).ordinal();
        if (age < RIPE_STAGE) { return; }
        Random random = world instanceof World ? ((World)world).rand : DROP_RANDOM;
        for (int i = 0; i < 3 + fortune; i++) { if (random.nextInt(8) <= age) { drops.add(new ItemStack(IFabItems.material, 1, FIBER_META)); } }
        drops.add(new ItemStack(IFabItems.seed));
    }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        checkAndDropBlock(world, pos, state);
        int age = state.getValue(TYPE).ordinal();
        if (world.getBlockState(pos).getBlock() != this || age >= RIPE_STAGE) { return; }
        int light = world.getLightFromNeighbors(pos.up());
        if (light < MIN_LIGHT) { return; }
        int period = BASE_PERIOD + (MAX_LIGHT - light) * LIGHT_PERIOD;
        if (world.getBlockState(pos.down()).getBlock().isFertile(world, pos.down())) { period = Math.max(1, period / 2); }
        if (random.nextInt(period) == 0) { setAge(world, pos, state, age + 1); }
    }

    private void setAge(World world, BlockPos pos, IBlockState state, int age) { world.setBlockState(pos, state.withProperty(TYPE, STAGES[Math.min(age, RIPE_STAGE)])); }

    @Override public boolean canGrow(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) { return state.getValue(TYPE) != FlaxStage.RIPE; }

    @Override public boolean canUseBonemeal(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return canGrow(world, pos, state, false); }

    @Override public void grow(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        int age = state.getValue(TYPE).ordinal();
        setAge(world, pos, state, age + 1 + random.nextInt(Math.max(1, RIPE_STAGE - age)));
    }
}
