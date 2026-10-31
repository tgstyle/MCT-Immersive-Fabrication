package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.fluid.IICPipe;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class TileEntityFluidPipe extends ICTileEntityBase implements IICPipe, IICFluidPipe {
    public static final int DEFAULT_SHAPE = 195;
    private static final EnumSet<EnumFacing> ALL = EnumSet.allOf(EnumFacing.class);
    private final FluidPipeHandler[] handlers = new FluidPipeHandler[6];
    @Nullable FluidPipeNetwork network;
    @Nullable private EnumDyeColor color;
    private byte connections;
    private byte closed;
    private byte blocked;
    private int shape = DEFAULT_SHAPE;

    @Override public void onLoad() { if (!world.isRemote) { ICUtils.addFutureServerTask(world, this::loadRefresh); } }

    private void loadRefresh() {
        if (isInvalid() || ICUtils.getExistingTileEntity(world, pos) != this) { return; }
        refresh();
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = ICUtils.getExistingTileEntity(world, pos.offset(f));
            if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).invalidateNetwork(); }
        }
    }

    @Override public void onChunkUnload() { invalidateNetwork(); }

    @Override public void invalidate() {
        super.invalidate();
        invalidateNetwork();
    }

    void invalidateNetwork() {
        if (network != null) { network.invalidate(); }
        network = null;
    }

    public void refresh() {
        if (world == null || world.isRemote) { return; }
        int conn = 0;
        int block = 0;
        for (EnumFacing f : EnumFacing.VALUES) {
            int bit = 1 << f.getIndex();
            BlockPos at = pos.offset(f);
            if (!world.isBlockLoaded(at)) {
                conn |= connections & bit;
                block |= blocked & bit;
                continue;
            }
            if ((closed & bit) != 0) { continue; }
            TileEntity te = ICUtils.getExistingTileEntity(world, at);
            EnumFacing back = f.getOpposite();
            if (te instanceof TileEntityFluidPipe) {
                TileEntityFluidPipe other = (TileEntityFluidPipe)te;
                if (other.color != color) { block |= bit; }
                else if (!other.isClosed(back)) { conn |= bit; }
            }
            else if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, back)) { conn |= bit; }
        }
        apply(conn, block, shapeOf(conn));
    }

    private void apply(int conn, int block, int newShape) {
        boolean linked = conn != (connections & 63);
        boolean exposure = block != (blocked & 63);
        if (!linked && !exposure && newShape == shape) { return; }
        connections = (byte)conn;
        blocked = (byte)block;
        shape = newShape;
        if (linked) {
            markDirty();
            invalidateNetwork();
            refreshNeighborShapes();
        }
        if (exposure) { notifyNonPipes(); }
        syncShape();
    }

    private void notifyAround() { ICUtils.improvedMarkBlockForUpdate(world, pos, null, ALL); }

    private void refreshNeighborShapes() {
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = ICUtils.getExistingTileEntity(world, pos.offset(f));
            if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).refreshShape(); }
        }
    }

    private void refreshShape() {
        int newShape = shapeOf(connections & 63);
        if (newShape == shape) { return; }
        shape = newShape;
        syncShape();
    }

    private void notifyNonPipes() {
        Block block = world.getBlockState(pos).getBlock();
        for (EnumFacing f : EnumFacing.VALUES) {
            BlockPos at = pos.offset(f);
            if (world.isBlockLoaded(at) && !(ICUtils.getExistingTileEntity(world, at) instanceof TileEntityFluidPipe)) { world.neighborChanged(at, block, pos); }
        }
    }

    private void syncShape() {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    private int shapeOf(int conn) {
        if (conn != 3 && conn != 12 && conn != 48) { return conn; }
        int low = Integer.numberOfTrailingZeros(conn);
        int flanges = sameStraight(EnumFacing.byIndex(low), conn) ? 0 : 64;
        return conn | flanges | (sameStraight(EnumFacing.byIndex(low + 1), conn) ? 0 : 128);
    }

    private boolean sameStraight(EnumFacing f, int conn) {
        TileEntity te = ICUtils.getExistingTileEntity(world, pos.offset(f));
        return te instanceof TileEntityFluidPipe && (((TileEntityFluidPipe)te).connections & 63) == conn;
    }

    private static int baseShape(int conn) { return conn == 3 || conn == 12 || conn == 48 ? conn | 192 : conn; }

    @Nullable public static EnumDyeColor stackColor(ItemStack stack) { return readColor(stack.getTagCompound()); }

    @Nullable private static EnumDyeColor readColor(@Nullable NBTTagCompound tag) { return tag != null && tag.hasKey("color", Constants.NBT.TAG_INT) ? base(EnumDyeColor.byMetadata(tag.getInteger("color"))) : null; }

    @Nullable private static EnumDyeColor base(@Nullable EnumDyeColor dye) { return dye == EnumDyeColor.SILVER ? null : dye; }

    public void placed(ItemStack stack) {
        EnumDyeColor dyed = stackColor(stack);
        if (dyed == null) { refresh(); }
        else { dye(dyed); }
    }

    public boolean dye(EnumDyeColor dye) {
        EnumDyeColor target = base(dye);
        if (target == color) { return false; }
        color = target;
        markDirty();
        invalidateNetwork();
        refresh();
        syncShape();
        notifyAround();
        return true;
    }

    public void toggleSide(EnumFacing side) { setClosed(side, !isClosed(side), true); }

    private void setClosed(EnumFacing side, boolean shut, boolean mirror) {
        if (isClosed(side) != shut) {
            closed = (byte)(closed ^ 1 << side.getIndex());
            markDirty();
            invalidateNetwork();
            refresh();
            notifyAround();
        }
        if (!mirror) { return; }
        TileEntity te = ICUtils.getExistingTileEntity(world, pos.offset(side));
        if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).setClosed(side.getOpposite(), shut, false); }
    }

    public void removed() {
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = ICUtils.getExistingTileEntity(world, pos.offset(f));
            if (te instanceof TileEntityFluidPipe) { ((TileEntityFluidPipe)te).setClosed(f.getOpposite(), false, false); }
        }
        invalidateNetwork();
    }

    boolean isClosed(EnumFacing f) { return (closed >> f.getIndex() & 1) != 0; }

    boolean isConnected(EnumFacing f) { return (connections >> f.getIndex() & 1) != 0; }

    private boolean exposed(EnumFacing f) { return ((closed | blocked) >> f.getIndex() & 1) == 0; }

    public int getConnections() { return connections & 63; }

    public int getShape() { return shape; }

    @Nullable public EnumDyeColor getColor() { return color; }

    int fillFrom(FluidStack resource, long origin, boolean doFill) {
        if (world == null || world.isRemote) { return 0; }
        FluidPipeNetwork net = network;
        if (net == null || !net.isValid()) { net = FluidPipeNetwork.build(this); }
        return net.fill(resource, origin, doFill);
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return facing != null && exposed(facing); }
        return super.hasCapability(capability, facing);
    }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return super.getCapability(capability, facing); }
        if (facing == null || !exposed(facing)) { return null; }
        FluidPipeHandler handler = handlers[facing.getIndex()];
        if (handler == null) {
            handler = new FluidPipeHandler(this, facing);
            handlers[facing.getIndex()] = handler;
        }
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(handler);
    }

    @Override public boolean hasCover() { return false; }

    @Override public void toggleSide(int side) { setClosed(EnumFacing.byIndex(side), false, false); }

    @Override public int[] getSideConfig() {
        int[] config = new int[6];
        for (int i = 0; i < 6; i++) { config[i] = (closed >> i & 1) != 0 ? -1 : 0; }
        return config;
    }

    @Override public boolean canOutputPressurized(boolean consumePower) { return true; }

    @Override public boolean hasOutputConnection(EnumFacing side) { return exposed(side); }

    @Override public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        int oldShape = shape;
        EnumDyeColor oldColor = color;
        connections = nbt.getByte("connections");
        int[] config = nbt.getIntArray("sideConfig");
        int shut = 0;
        for (int i = 0; i < config.length && i < 6; i++) { if (config[i] == -1) { shut |= 1 << i; } }
        closed = (byte)shut;
        color = readColor(nbt);
        shape = nbt.hasKey("shape", Constants.NBT.TAG_BYTE) ? nbt.getByte("shape") & 255 : baseShape(connections & 63);
        if (world != null && world.isRemote && (oldShape != shape || oldColor != color)) { world.markBlockRangeForRenderUpdate(pos, pos); }
    }

    @Override public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        nbt.setByte("connections", connections);
        nbt.setIntArray("sideConfig", getSideConfig());
        if (color != null) { nbt.setInteger("color", color.getMetadata()); }
        if (descPacket) { nbt.setByte("shape", (byte)shape); }
    }
}
