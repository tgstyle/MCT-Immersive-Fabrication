package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.multiblocks.IFabShapes;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;

import com.immersiveconvergence.api.multiblock.FormationCandidate;
import com.immersiveconvergence.api.multiblock.MachineTemplateMultiblock;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public class CokeOvenMultiblock extends MachineTemplateMultiblock {
    public static final CokeOvenMultiblock INSTANCE = new CokeOvenMultiblock();
    private static final BlockPos CHAMBER = new BlockPos(1, 1, 1);

    public CokeOvenMultiblock() { super(ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "multiblocks/coke_oven"), () -> IFabShapes.get("coke_oven"), IFabMultiblocks.COKE_OVEN); }

    @Override public boolean createStructure(Level world, BlockPos pos, Direction side, Player player) {
        Rotation rotation = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
        if (rotation == null) { return false; }
        BlockPos chamber = FormationCandidate.atTrigger(pos, getTriggerOffset(), rotation, Mirror.NONE).toAbsolute(CHAMBER);
        return world.isEmptyBlock(chamber) && super.createStructure(world, pos, side, player);
    }
}
