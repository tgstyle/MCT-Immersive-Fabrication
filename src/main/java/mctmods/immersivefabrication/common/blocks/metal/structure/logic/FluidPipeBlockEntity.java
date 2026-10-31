package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.api.util.ICUtils;

import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidPipeBlockEntity extends BaseBlockEntity implements IICFluidPipe {
    public static final int DEFAULT_SHAPE = 195;
    public static final ModelProperty<Integer> SHAPE = new ModelProperty<>();
    public static final String COLOR_KEY = "color";
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> caps = new EnumMap<>(Direction.class);
    @Nullable FluidPipeNetwork network;
    private int color = -1;
    private byte connections;
    private byte closed;
    private byte blocked;
    private int shape = DEFAULT_SHAPE;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) { super(IFabBlockEntities.FLUID_PIPE.get(), pos, state); }

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) { ICUtils.addFutureServerTask(level, this::loadRefresh); }
    }

    private void loadRefresh() {
        Level world = level;
        if (world == null || isRemoved() || SafeChunkUtils.getExistingBlockEntity(world, worldPosition) != this) { return; }
        refresh();
        for (Direction f : DirectionUtils.VALUES) { if (SafeChunkUtils.getExistingBlockEntity(world, worldPosition.relative(f)) instanceof FluidPipeBlockEntity other) { other.invalidateNetwork(); } }
    }

    @Override public void onChunkUnloaded() {
        super.onChunkUnloaded();
        invalidateNetwork();
    }

    @Override public void setRemoved() {
        super.setRemoved();
        invalidateNetwork();
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        releaseCaps(63);
    }

    void invalidateNetwork() {
        if (network != null) { network.invalidate(); }
        network = null;
    }

    private void releaseCaps(int mask) {
        for (Direction f : DirectionUtils.VALUES) {
            if ((mask >> f.get3DDataValue() & 1) == 0) { continue; }
            LazyOptional<IFluidHandler> cap = caps.remove(f);
            if (cap != null) { cap.invalidate(); }
        }
    }

    public void refresh() {
        Level world = level;
        if (world == null || world.isClientSide) { return; }
        int conn = 0;
        int block = 0;
        for (Direction f : DirectionUtils.VALUES) {
            int bit = 1 << f.get3DDataValue();
            BlockPos at = worldPosition.relative(f);
            if (!world.isLoaded(at)) {
                conn |= connections & bit;
                block |= blocked & bit;
                continue;
            }
            if ((closed & bit) != 0) { continue; }
            BlockEntity be = SafeChunkUtils.getExistingBlockEntity(world, at);
            Direction back = f.getOpposite();
            if (be instanceof FluidPipeBlockEntity other) {
                if (other.color != color) { block |= bit; }
                else if (!other.isClosed(back)) { conn |= bit; }
            }
            else if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER, back).isPresent()) { conn |= bit; }
        }
        apply(conn, block, shapeOf(conn));
    }

    private void apply(int conn, int block, int newShape) {
        boolean linked = conn != (connections & 63);
        int exposure = block ^ (blocked & 63);
        if (!linked && exposure == 0 && newShape == shape) { return; }
        connections = (byte) conn;
        blocked = (byte) block;
        shape = newShape;
        if (linked) {
            setChanged();
            invalidateNetwork();
            refreshNeighborShapes();
        }
        if (exposure != 0) {
            releaseCaps(exposure);
            notifyNonPipes();
        }
        syncShape();
    }

    private void notifyAround() {
        markContainingBlockForUpdate(null);
        ICUtils.updateObservers(getLevelNonnull(), worldPosition);
    }

    private void refreshNeighborShapes() {
        Level world = getLevelNonnull();
        for (Direction f : DirectionUtils.VALUES) { if (SafeChunkUtils.getExistingBlockEntity(world, worldPosition.relative(f)) instanceof FluidPipeBlockEntity other) { other.refreshShape(); } }
    }

    private void refreshShape() {
        int newShape = shapeOf(connections & 63);
        if (newShape == shape) { return; }
        shape = newShape;
        syncShape();
    }

    private void notifyNonPipes() {
        Level world = getLevelNonnull();
        Block block = getBlockState().getBlock();
        for (Direction f : DirectionUtils.VALUES) {
            BlockPos at = worldPosition.relative(f);
            if (world.isLoaded(at) && !(SafeChunkUtils.getExistingBlockEntity(world, at) instanceof FluidPipeBlockEntity)) { world.neighborChanged(at, block, worldPosition); }
        }
    }

    private void syncShape() {
        BlockState state = getBlockState();
        getLevelNonnull().sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    private int shapeOf(int conn) {
        if (conn != 3 && conn != 12 && conn != 48) { return conn; }
        int low = Integer.numberOfTrailingZeros(conn);
        int flanges = sameStraight(Direction.from3DDataValue(low), conn) ? 0 : 64;
        return conn | flanges | (sameStraight(Direction.from3DDataValue(low + 1), conn) ? 0 : 128);
    }

    private boolean sameStraight(Direction f, int conn) { return SafeChunkUtils.getExistingBlockEntity(getLevelNonnull(), worldPosition.relative(f)) instanceof FluidPipeBlockEntity other && (other.connections & 63) == conn; }

    private static int baseShape(int conn) { return conn == 3 || conn == 12 || conn == 48 ? conn | 192 : conn; }

    public static int stackColor(ItemStack stack) { return readColor(stack.getTag()); }

    private static int readColor(@Nullable CompoundTag tag) { return tag != null && tag.contains(COLOR_KEY, Tag.TAG_INT) ? base(tag.getInt(COLOR_KEY)) : -1; }

    private static int base(int color) { return color < 0 || color == DyeColor.LIGHT_GRAY.getId() ? -1 : DyeColor.byId(color).getId(); }

    public void placed(ItemStack stack) {
        int dyed = stackColor(stack);
        if (dyed < 0) { refresh(); }
        else { dye(DyeColor.byId(dyed)); }
    }

    public boolean dye(DyeColor dye) {
        int target = base(dye.getId());
        if (target == color) { return false; }
        color = target;
        setChanged();
        invalidateNetwork();
        refresh();
        notifyAround();
        return true;
    }

    public void toggleSide(Direction side) { setClosed(side, !isClosed(side), true); }

    private void setClosed(Direction side, boolean shut, boolean mirror) {
        if (isClosed(side) != shut) {
            closed = (byte) (closed ^ 1 << side.get3DDataValue());
            releaseCaps(1 << side.get3DDataValue());
            setChanged();
            invalidateNetwork();
            refresh();
            notifyAround();
        }
        if (!mirror) { return; }
        if (SafeChunkUtils.getExistingBlockEntity(getLevelNonnull(), worldPosition.relative(side)) instanceof FluidPipeBlockEntity other) { other.setClosed(side.getOpposite(), shut, false); }
    }

    public void removed() {
        Level world = getLevelNonnull();
        for (Direction f : DirectionUtils.VALUES) { if (SafeChunkUtils.getExistingBlockEntity(world, worldPosition.relative(f)) instanceof FluidPipeBlockEntity other) { other.setClosed(f.getOpposite(), false, false); } }
        invalidateNetwork();
    }

    boolean isClosed(Direction f) { return (closed >> f.get3DDataValue() & 1) != 0; }

    boolean isConnected(Direction f) { return (connections >> f.get3DDataValue() & 1) != 0; }

    private boolean exposed(Direction f) { return ((closed | blocked) >> f.get3DDataValue() & 1) == 0; }

    public int getConnections() { return connections & 63; }

    public byte getShape() { return (byte) shape; }

    public int getColor() { return color; }

    int fillFrom(FluidStack resource, long origin, boolean execute) {
        if (level == null || level.isClientSide) { return 0; }
        FluidPipeNetwork net = network;
        if (net == null || !net.isValid()) { net = FluidPipeNetwork.build(this); }
        return net.fill(resource, origin, execute);
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability != ForgeCapabilities.FLUID_HANDLER) { return super.getCapability(capability, side); }
        if (side == null || !exposed(side)) { return LazyOptional.empty(); }
        LazyOptional<IFluidHandler> cap = caps.get(side);
        if (cap == null) {
            FluidPipeHandler handler = new FluidPipeHandler(this, side);
            cap = LazyOptional.of(() -> handler);
            caps.put(side, cap);
        }
        return cap.cast();
    }

    @Override public boolean canOutputPressurized() { return true; }

    @Override @Nonnull public ModelData getModelData() { return ModelData.builder().with(SHAPE, getShape() & 0xFF).build(); }

    private int[] sideConfig() {
        int[] config = new int[6];
        for (int i = 0; i < 6; i++) { config[i] = (closed >> i & 1) != 0 ? -1 : 0; }
        return config;
    }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        int oldShape = shape;
        int oldColor = color;
        connections = nbt.getByte("connections");
        int[] config = nbt.getIntArray("sideConfig");
        int shut = 0;
        for (int i = 0; i < config.length && i < 6; i++) { if (config[i] == -1) { shut |= 1 << i; } }
        closed = (byte) shut;
        color = readColor(nbt);
        shape = nbt.contains("shape", Tag.TAG_BYTE) ? nbt.getByte("shape") & 255 : baseShape(connections & 63);
        if (level != null && level.isClientSide && (oldShape != shape || oldColor != color)) {
            requestModelDataUpdate();
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        nbt.putByte("connections", connections);
        nbt.putIntArray("sideConfig", sideConfig());
        if (color >= 0) { nbt.putInt(COLOR_KEY, color); }
        if (descPacket) { nbt.putByte("shape", (byte) shape); }
    }
}
