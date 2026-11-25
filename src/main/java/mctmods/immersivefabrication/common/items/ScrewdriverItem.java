package mctmods.immersivefabrication.common.items;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityHeatingElement;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICItemBase;
import com.immersiveconvergence.api.multiblock.MultiblockRegistry;
import com.immersiveconvergence.api.wires.fittings.ICTileEntityFitting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public class ScrewdriverItem extends ICItemBase {
    public static final String TOOL_CLASS = "IFAB_SCREWDRIVER";

    public ScrewdriverItem(BlockContext context) { super(context, "screwdriver", 1, "screwdriver"); }

    @Override @Nonnull public Set<String> getToolClasses(@Nonnull ItemStack stack) { return Collections.singleton(TOOL_CLASS); }

    @Override public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) { return true; }

    @Override @Nonnull public EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null && !player.isSneaking()) {
            if (!world.isRemote && MultiblockRegistry.formFirstMatching(world, pos, side, player, player.getHeldItem(hand), "IFab:CokeOven"::equals)) { return EnumActionResult.SUCCESS; }
            if (world.isRemote && CokeOvenMultiblock.instance.isBlockTrigger(world.getBlockState(pos))) { return EnumActionResult.SUCCESS; }
        }
        if (tile instanceof TileEntityIndustrialPump) { return configurePump((TileEntityIndustrialPump)tile, world, player, side); }
        if (tile instanceof TileEntityHeatingElement) { return turnHeater((TileEntityHeatingElement)tile, world, player, side); }
        if (!(tile instanceof ICTileEntityFitting) || !((ICTileEntityFitting)tile).platable()) { return EnumActionResult.PASS; }
        if (!world.isRemote) { ((ICTileEntityFitting)tile).togglePlate(); }
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult configurePump(TileEntityIndustrialPump pump, World world, EntityPlayer player, EnumFacing side) {
        EnumFacing face = player.isSneaking() ? side.getOpposite() : side;
        if (!pump.configurable(face)) { return EnumActionResult.PASS; }
        if (!world.isRemote) { pump.cycle(face); }
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult turnHeater(TileEntityHeatingElement heater, World world, EntityPlayer player, EnumFacing side) {
        if (!world.isRemote) { heater.turn(side, player.isSneaking()); }
        return EnumActionResult.SUCCESS;
    }

    @Override public boolean hasContainerItem(@Nonnull ItemStack stack) { return true; }

    @Override @Nonnull public ItemStack getContainerItem(@Nonnull ItemStack stack) { return stack.copy(); }
}
