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
    public record Fire(int flammability, int spread) {}

    private final Fire fire;

    public IFabFluidBlock(FluidEntry entry, Properties props, Fire fire) {
        super(entry, props);
        this.fire = fire;
    }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return fire.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return fire.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return fire.spread(); }

    @Override public void onCaughtFire(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) { level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); }
}
