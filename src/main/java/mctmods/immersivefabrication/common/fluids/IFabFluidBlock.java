package mctmods.immersivefabrication.common.fluids;

import com.immersiveconvergence.api.fluid.BaseFluidBlock;
import com.immersiveconvergence.api.fluid.FluidEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IFabFluidBlock extends BaseFluidBlock {
    private final int flammability;
    private final int fireSpread;

    public IFabFluidBlock(FluidEntry entry, Properties props, int flammability, int fireSpread) {
        super(entry, props);
        this.flammability = flammability;
        this.fireSpread = fireSpread;
    }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return fireSpread; }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) { return flammability > 0; }

    @Override public void onCaughtFire(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) { level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); }
}
