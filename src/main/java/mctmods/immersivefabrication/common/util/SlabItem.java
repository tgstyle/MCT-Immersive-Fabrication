package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.ICItemBlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SlabItem extends ICItemBlockBase {

    public SlabItem(Block block) { super(block); }

    @Override @Nonnull public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos target = mergeTarget(world, pos, side, stack);
        if (target == null || !player.canPlayerEdit(target, side, stack)) { return super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ); }
        IBlockState full = ((SlabBlock<?>)block).getFamily().full.getStateFromMeta(stack.getMetadata());
        AxisAlignedBB box = full.getCollisionBoundingBox(world, target);
        if (box != null && !world.checkNoEntityCollision(box.offset(target)) || !world.setBlockState(target, full, 11)) { return EnumActionResult.FAIL; }
        SoundType sound = full.getBlock().getSoundType(full, world, target, player);
        world.playSound(player, target, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        if (!player.capabilities.isCreativeMode) { stack.shrink(1); }
        return EnumActionResult.SUCCESS;
    }

    private BlockPos mergeTarget(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        IBlockState clicked = world.getBlockState(pos);
        if (matches(clicked, stack)) {
            boolean top = ((SlabBlock<?>)clicked.getBlock()).isTop();
            if (side == EnumFacing.UP && !top || side == EnumFacing.DOWN && top) { return pos; }
        }
        BlockPos front = pos.offset(side);
        return matches(world.getBlockState(front), stack) ? front : null;
    }

    private boolean matches(IBlockState state, ItemStack stack) {
        Block other = state.getBlock();
        return other instanceof SlabBlock && ((SlabBlock<?>)other).getFamily() == ((SlabBlock<?>)block).getFamily() && other.getMetaFromState(state) == stack.getMetadata();
    }
}
