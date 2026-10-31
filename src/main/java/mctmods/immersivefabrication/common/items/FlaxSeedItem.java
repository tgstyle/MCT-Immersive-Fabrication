package mctmods.immersivefabrication.common.items;

import com.immersiveconvergence.api.block.BlockContext;
import com.immersiveconvergence.api.block.ICItemBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;

public class FlaxSeedItem extends ICItemBase implements IPlantable {
    private final Block crop;

    public FlaxSeedItem(BlockContext context, Block crop) {
        super(context, "seed", 64, "flax");
        this.crop = crop;
    }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos above = pos.up();
        if (side != EnumFacing.UP || !player.canPlayerEdit(pos, side, stack) || !player.canPlayerEdit(above, side, stack)) { return EnumActionResult.PASS; }
        IBlockState soil = world.getBlockState(pos);
        if (!soil.getBlock().canSustainPlant(soil, world, pos, EnumFacing.UP, this) || !world.isAirBlock(above)) { return EnumActionResult.PASS; }
        world.setBlockState(above, crop.getDefaultState());
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    @Override @Nonnull public EnumPlantType getPlantType(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return EnumPlantType.Crop; }

    @Override @Nonnull public IBlockState getPlant(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return crop.getDefaultState(); }
}
