package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.common.multiblocks.IFabShapes;
import mctmods.immersivefabrication.common.multiblocks.stone.StoneMultiblockType;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenSlave;
import mctmods.immersivefabrication.core.registration.IFabBlocks;

import com.immersiveconvergence.api.multiblock.MachineTemplateMultiblock;
import com.immersiveconvergence.api.multiblock.TemplateMultiblock;
import com.immersiveconvergence.api.util.ICUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CokeOvenMultiblock extends MachineTemplateMultiblock<TileEntityCokeOvenSlave> {
    public static final CokeOvenMultiblock instance = new CokeOvenMultiblock();

    public CokeOvenMultiblock() { super("IFab:CokeOven", IFabShapes.get("coke_oven"), ICUtils.stateOf(IFabBlocks.stoneMultiblock, StoneMultiblockType.COKE_OVEN), ICUtils.stateOf(IFabBlocks.stoneMultiblock, StoneMultiblockType.COKE_OVEN_SLAVE), 0.5, 1.5, 1.5, 4); }

    @Override protected boolean canMirror() { return false; }

    @Override protected boolean isValid(World world, BlockPos pos, EnumFacing side, BlockPos trigger, boolean mirror) {
        if (!super.isValid(world, pos, side, trigger, mirror)) { return false; }
        BlockPos chamber = TemplateMultiblock.localToWorld(originFor(pos, side, trigger, mirror), localX(1, mirror), 1, 1, side);
        return world.isAirBlock(chamber);
    }
}
