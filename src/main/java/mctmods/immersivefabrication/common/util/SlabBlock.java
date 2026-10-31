package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.ICBlockBase;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class SlabBlock<E extends Enum<E> & ICBlockBase.IBlockEnum> extends ICBlockBase<E> {
    private static final AxisAlignedBB BOTTOM_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    private static final AxisAlignedBB TOP_BOX = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    private final SlabFamily<E> family;
    private final boolean top;

    public SlabBlock(SlabFamily<E> family, String name, boolean top, Class<? extends ItemBlock> itemBlock) {
        super(family.full.context, name, family.full.getDefaultState().getMaterial(), PropertyEnum.create("type", family.full.property.getValueClass()), itemBlock);
        this.family = family;
        this.top = top;
        setAllNotNormalBlock();
        this.useNeighborBrightness = true;
        this.lightOpacity = 0;
    }

    public SlabFamily<E> getFamily() { return family; }

    public boolean isTop() { return top; }

    @Override @Nonnull public String getTranslationKey(ItemStack stack) {
        String subName = getStateFromMeta(stack.getItemDamage()).getValue(property).getName().toLowerCase(Locale.ROOT);
        return "tile." + context.modid + "." + family.bottom.name + "." + subName;
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) { return top ? TOP_BOX : BOTTOM_BOX; }

    @SuppressWarnings("deprecation")
    @Override public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) { return side == (top ? EnumFacing.UP : EnumFacing.DOWN); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return face == (top ? EnumFacing.UP : EnumFacing.DOWN) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED; }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer) {
        boolean upper = facing == EnumFacing.DOWN || facing != EnumFacing.UP && hitY > 0.5F;
        return (upper ? family.top : family.bottom).getStateFromMeta(meta);
    }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) { return Item.getItemFromBlock(family.bottom); }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return new ItemStack(family.bottom, 1, getMetaFromState(state)); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (!top) { super.getSubBlocks(tab, list); }
    }
}
