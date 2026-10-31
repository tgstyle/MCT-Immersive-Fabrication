package mctmods.immersivefabrication.common.blocks.metal.structure;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityFluidPipe;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityHeatingElement;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.TileEntityIndustrialPump;
import mctmods.immersivefabrication.common.util.PipeArms;
import mctmods.immersivefabrication.common.util.PumpFaces;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.api.block.ICProperties;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StructureBlock extends ICBlockBase<StructureType> {
    public static final IUnlistedProperty<Integer> SHAPE = Properties.toUnlisted(PropertyInteger.create("shape", 0, 255));
    public static final IUnlistedProperty<Integer> COLOR = Properties.toUnlisted(PropertyInteger.create("color", 0, 15));
    private static final AxisAlignedBB PIPE_BOX = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB PUMP_TOP_BOX = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.9375D);
    private static final AxisAlignedBB[] PIPE_BOXES = pipeBoxes();
    private static final String[] DYES = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

    public StructureBlock() {
        super(IFabContext.CONTEXT, "structure", Material.IRON, PropertyEnum.create("type", StructureType.class), ICItemBlockBase.class, ICProperties.FACING_ALL, ICProperties.BOOLEANS[0], SHAPE, COLOR);
        metaLightOpacities.put(StructureType.FLUID_PIPE.getMeta(), 0);
        metaLightOpacities.put(StructureType.INDUSTRIAL_PUMP_TOP.getMeta(), 0);
    }

    private static AxisAlignedBB[] pipeBoxes() {
        AxisAlignedBB[] boxes = new AxisAlignedBB[256];
        for (int key = 0; key < 256; key++) { boxes[key] = pipeBox(key); }
        return boxes;
    }

    private static AxisAlignedBB pipeBox(int key) {
        int mask = key & 63;
        int flanged = mask == 3 || mask == 12 || mask == 48 ? (key >> 6 & 3) << Integer.numberOfTrailingZeros(mask) : mask;
        double[] lo = {0.25D, 0.25D, 0.25D};
        double[] hi = {0.75D, 0.75D, 0.75D};
        for (int f = 0; f < 6; f++) {
            int axis = f >> 1;
            if ((mask >> f & 1) != 0) {
                if ((f & 1) == 0) { lo[axis] = 0.0D; }
                else { hi[axis] = 1.0D; }
            }
            if ((flanged >> f & 1) == 0) { continue; }
            for (int a = 0; a < 3; a++) {
                if (a == axis) { continue; }
                lo[a] = Math.min(lo[a], 0.125D);
                hi[a] = Math.max(hi[a], 0.875D);
            }
        }
        return new AxisAlignedBB(lo[2], lo[0], lo[1], hi[2], hi[0], hi[1]);
    }

    @Nullable private static TileEntity existing(IBlockAccess world, BlockPos pos) { return world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : ICUtils.getExistingTileEntity(world, pos); }

    @Nullable public static TileEntityFluidPipe pipe(IBlockAccess world, BlockPos pos) {
        TileEntity te = existing(world, pos);
        return te instanceof TileEntityFluidPipe ? (TileEntityFluidPipe)te : null;
    }

    @Nullable private static EnumDyeColor dyeColor(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            for (int i = 0; i < DYES.length; i++) { if (DYES[i].equals(name)) { return EnumDyeColor.byDyeDamage(i); } }
        }
        return null;
    }

    private boolean isType(IBlockState state, StructureType type) { return property != null && state.getBlock() == this && state.getValue(property) == type; }

    @Override protected boolean normalBlockCheck(IBlockState state) { return !isType(state, StructureType.FLUID_PIPE) && !isType(state, StructureType.INDUSTRIAL_PUMP_TOP); }

    @Override public boolean hasTileEntity(@Nonnull IBlockState state) { return isType(state, StructureType.FLUID_PIPE) || isType(state, StructureType.HEATING_ELEMENT) || isType(state, StructureType.INDUSTRIAL_PUMP) || isType(state, StructureType.INDUSTRIAL_PUMP_TOP); }

    @Override @Nullable public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        if (isType(state, StructureType.FLUID_PIPE)) { return new TileEntityFluidPipe(); }
        if (isType(state, StructureType.INDUSTRIAL_PUMP)) { return new TileEntityIndustrialPump(); }
        if (isType(state, StructureType.INDUSTRIAL_PUMP_TOP)) { return new TileEntityIndustrialPump().asDummy(); }
        return isType(state, StructureType.HEATING_ELEMENT) ? new TileEntityHeatingElement() : null;
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        TileEntity te = isType(state, StructureType.HEATING_ELEMENT) ? existing(world, pos) : null;
        if (!(te instanceof TileEntityHeatingElement)) { return state.withProperty(ICProperties.FACING_ALL, EnumFacing.NORTH); }
        TileEntityHeatingElement heater = (TileEntityHeatingElement)te;
        return state.withProperty(ICProperties.FACING_ALL, heater.getFacing()).withProperty(ICProperties.BOOLEANS[0], heater.getIsActive());
    }

    @Override @Nonnull public IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        if (!(state instanceof IExtendedBlockState)) { return state; }
        IExtendedBlockState extended = (IExtendedBlockState)state;
        if (isType(state, StructureType.INDUSTRIAL_PUMP)) {
            TileEntity pump = existing(world, pos);
            return extended.withProperty(SHAPE, pump instanceof TileEntityIndustrialPump ? ((TileEntityIndustrialPump)pump).faceCode() : PumpFaces.DEFAULT_CODE);
        }
        if (!isType(state, StructureType.FLUID_PIPE)) { return state; }
        TileEntityFluidPipe pipe = pipe(world, pos);
        if (pipe == null) { return extended.withProperty(SHAPE, TileEntityFluidPipe.DEFAULT_SHAPE); }
        EnumDyeColor color = pipe.getColor();
        extended = extended.withProperty(SHAPE, pipe.getShape());
        return color == null ? extended : extended.withProperty(COLOR, color.getMetadata());
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        if (isType(state, StructureType.FLUID_PIPE)) {
            TileEntityFluidPipe pipe = pipe(world, pos);
            return pipe == null ? PIPE_BOX : PIPE_BOXES[pipe.getShape() & 255];
        }
        if (isType(state, StructureType.INDUSTRIAL_PUMP_TOP)) { return PUMP_TOP_BOX; }
        return FULL_BLOCK_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override @Nonnull public EnumPushReaction getPushReaction(@Nonnull IBlockState state) { return isType(state, StructureType.INDUSTRIAL_PUMP) || isType(state, StructureType.INDUSTRIAL_PUMP_TOP) ? EnumPushReaction.BLOCK : EnumPushReaction.NORMAL; }

    @SuppressWarnings("deprecation")
    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos) {
        if (!world.isRemote && (isType(state, StructureType.INDUSTRIAL_PUMP) || isType(state, StructureType.INDUSTRIAL_PUMP_TOP))) { world.getTileEntity(pos); if (isType(state, StructureType.INDUSTRIAL_PUMP_TOP)) { world.getTileEntity(pos.down()); } }
        if (world.isRemote || !isType(state, StructureType.FLUID_PIPE)) { return; }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).refresh(); }
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!isType(state, StructureType.FLUID_PIPE)) { return false; }
        ItemStack held = player.getHeldItem(hand);
        if (ICUtils.isToggleTool(held)) {
            TileEntity pipe = world.isRemote ? null : world.getTileEntity(pos);
            if (pipe instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)pipe).toggleSide(PipeArms.at(side, hitX, hitY, hitZ)); }
            return true;
        }
        EnumDyeColor dye = player.isSneaking() ? null : dyeColor(held);
        if (dye == null) { return false; }
        if (world.isRemote) { return true; }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityFluidPipe)) { return true; }
        if (((TileEntityFluidPipe)te).dye(dye) && !player.capabilities.isCreativeMode) { held.shrink(1); }
        return true;
    }

    @Override public boolean canITBlockBePlaced(World world, BlockPos pos, IBlockState newState, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack) {
        if (!isType(newState, StructureType.INDUSTRIAL_PUMP)) { return true; }
        BlockPos above = pos.up();
        return !world.isOutsideBuildHeight(above) && world.getBlockState(above).getBlock().isReplaceable(world, above);
    }

    @Override public void onITBlockPlacedBy(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {
        if (isType(state, StructureType.INDUSTRIAL_PUMP)) { world.setBlockState(pos.up(), getDefaultState().withProperty(property, StructureType.INDUSTRIAL_PUMP_TOP)); }
        TileEntity heater = isType(state, StructureType.HEATING_ELEMENT) ? world.getTileEntity(pos) : null;
        if (heater instanceof TileEntityHeatingElement) { ((TileEntityHeatingElement)heater).placed(placer, side, hitX, hitY, hitZ); }
        if (world.isRemote || !isType(state, StructureType.FLUID_PIPE)) { return; }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).placed(stack); }
    }

    @Override public void onBlockHarvested(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) {
        BlockPos below = pos.down();
        if (isType(state, StructureType.INDUSTRIAL_PUMP_TOP) && isType(world.getBlockState(below), StructureType.INDUSTRIAL_PUMP)) {
            if (player.capabilities.isCreativeMode) { world.setBlockToAir(below); }
            else { world.destroyBlock(below, true); }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (willHarvest && isType(state, StructureType.FLUID_PIPE)) { return true; }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override public void harvestBlock(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        if (isType(state, StructureType.FLUID_PIPE)) { world.setBlockToAir(pos); }
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        BlockPos above = pos.up();
        if (isType(state, StructureType.INDUSTRIAL_PUMP) && isType(world.getBlockState(above), StructureType.INDUSTRIAL_PUMP_TOP)) { world.setBlockToAir(above); }
        if (isType(state, StructureType.FLUID_PIPE)) {
            TileEntityFluidPipe pipe = pipe(world, pos);
            if (pipe != null) { pipe.removed(); }
        }
        super.breakBlock(world, pos, state);
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (isType(state, StructureType.INDUSTRIAL_PUMP_TOP)) { return; }
        if (isType(state, StructureType.FLUID_PIPE)) { drops.add(pipeStack(world, pos)); }
        else { super.getDrops(drops, world, pos, state, fortune); }
    }

    @Override @Nonnull public ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        if (isType(state, StructureType.FLUID_PIPE)) { return pipeStack(world, pos); }
        int meta = isType(state, StructureType.INDUSTRIAL_PUMP_TOP) ? StructureType.INDUSTRIAL_PUMP.getMeta() : getMetaFromState(state);
        return new ItemStack(this, 1, meta);
    }

    private ItemStack pipeStack(IBlockAccess world, BlockPos pos) {
        ItemStack stack = new ItemStack(this, 1, StructureType.FLUID_PIPE.getMeta());
        TileEntityFluidPipe pipe = pipe(world, pos);
        EnumDyeColor color = pipe == null ? null : pipe.getColor();
        if (color != null) { stack.setTagInfo("color", new NBTTagInt(color.getMetadata())); }
        return stack;
    }
}
