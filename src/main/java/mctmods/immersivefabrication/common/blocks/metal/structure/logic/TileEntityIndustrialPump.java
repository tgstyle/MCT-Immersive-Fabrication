package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.util.FluidBlocks;
import mctmods.immersivefabrication.common.util.PumpFaces;
import mctmods.immersivefabrication.common.util.PumpSources;
import mctmods.immersivefabrication.core.IFabCommonConfig;

import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.energy.ICFluxWrapper;
import com.immersiveconvergence.api.energy.IICInternalFluxHandler;
import com.immersiveconvergence.api.fluid.ICPipes;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.fluid.IICPipe;
import com.immersiveconvergence.api.util.ICFluidTank;
import com.immersiveconvergence.api.util.ICFluxStorage;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityIndustrialPump extends ICTileEntityBase implements ITickable, IICInternalFluxHandler {
    private static final int CAPACITY = 8000;
    private static final int TANK = 4000;
    private static final int CONSUMPTION = 250;
    private static final int ACCELERATE = 5;
    private static final int PERIOD = 40;
    private static final int KEEP_PERIOD = 20;
    private static final int ACTIVE_HOLD = PERIOD + 1;
    private static final double DEGREES_PER_TICK = 72.0D;
    final ICFluidTank tank = new ICFluidTank(TANK, this::markDirty);
    private final ICFluxStorage energy = new ICFluxStorage(CAPACITY);
    private final PumpArea area = new PumpArea();
    private final PumpFluidHandler[] handlers = new PumpFluidHandler[6];
    private int[] sideConfig = PumpFaces.defaults();
    private boolean dummy;
    private boolean active;
    private long lastMoved = -ACTIVE_HOLD - 1;
    private float angle;
    private double spinStamp = Double.NaN;
    @Nullable private ICFluxWrapper wrapper;

    public TileEntityIndustrialPump asDummy() {
        dummy = true;
        return this;
    }

    public int config(EnumFacing side) { return sideConfig[side.getIndex()]; }

    public int faceCode() { return PumpFaces.encode(sideConfig); }

    public boolean configurable(EnumFacing side) { return !dummy && side != EnumFacing.UP; }

    public void cycle(EnumFacing side) {
        sideConfig[side.getIndex()] = PumpFaces.next(side, sideConfig[side.getIndex()]);
        area.clear();
        markDirty();
        markContainingBlockForUpdate(null);
    }

    public boolean isDummy() { return dummy; }

    public float spinAngle(double time) {
        if (!Double.isNaN(spinStamp) && active) { angle = (float)((angle + Math.max(0.0D, time - spinStamp) * DEGREES_PER_TICK) % 360.0D); }
        spinStamp = time;
        return angle;
    }

    @SideOnly(Side.CLIENT) @Override @Nonnull public AxisAlignedBB getRenderBoundingBox() { return new AxisAlignedBB(pos, pos.add(1, 2, 1)); }

    @Override public void update() {
        if (dummy || world.isRemote) { return; }
        boolean moved = false;
        FluidStack held = tank.getFluid();
        if (held != null) {
            int sent = output(held);
            tank.drain(sent, true);
            moved = sent > 0;
        }
        if (world.getRedstonePowerFromNeighbors(pos) > 0 || world.getRedstonePowerFromNeighbors(pos.up()) > 0) {
            for (EnumFacing side : EnumFacing.VALUES) { if (sideConfig[side.getIndex()] == PumpFaces.INPUT && pull(side)) { moved = true; } }
            if (pumpArea()) { moved = true; }
        }
        area.scan(world);
        long time = world.getTotalWorldTime();
        if (moved) { lastMoved = time; }
        boolean running = time - lastMoved <= ACTIVE_HOLD;
        if (running == active) { return; }
        active = running;
        syncToTrackingClients();
    }

    private boolean pull(EnumFacing side) {
        TileEntity source = ICUtils.getExistingTileEntity(world, pos.offset(side));
        IFluidHandler handler = source == null ? null : source.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
        if (handler == null) { return false; }
        FluidStack offered = handler.drain(ICPipes.getTransferableAmount(false), false);
        if (offered == null || offered.amount <= 0) { return false; }
        IICFluidPipe.stripPressure(offered);
        int sent = output(offered);
        handler.drain(sent, true);
        return sent > 0;
    }

    private boolean pumpArea() {
        int phase = (int)Math.floorMod(world.getTotalWorldTime() - (pos.getX() ^ pos.getZ()), PERIOD);
        if (phase % KEEP_PERIOD != 0) { return false; }
        BlockPos target = area.target();
        if (target == null) {
            if (phase == 0 && !area.scanning()) { area.restart(pos, sideConfig); }
            return false;
        }
        FluidStack stack = FluidBlocks.source(world, target);
        if (stack == null || !PumpSources.pumps(stack.getFluid())) {
            area.dropTarget();
            return false;
        }
        IBlockState replacement = PumpSources.replacement(stack.getFluid());
        if (replacement == null && FluidBlocks.adjacentSources(world, target, stack.getFluid()) < IFabCommonConfig.pump.pump_keep_min_sources) {
            area.dropTarget();
            return false;
        }
        if (replacement != null && phase != 0) { return false; }
        if (tank.fill(stack, false) != stack.amount || energy.extractEnergy(CONSUMPTION, true) < CONSUMPTION) { return false; }
        energy.extractEnergy(CONSUMPTION, false);
        if (replacement != null) {
            FluidBlocks.take(world, target, replacement);
            area.dropTarget();
        }
        tank.fill(stack, true);
        markDirty();
        return true;
    }

    private int output(FluidStack fs) {
        if (fs.amount <= 0) { return 0; }
        IFluidHandler[] targets = new IFluidHandler[6];
        int[] room = new int[6];
        boolean[] accelerated = new boolean[6];
        int sum = 0;
        int last = -1;
        for (EnumFacing side : EnumFacing.VALUES) {
            if (sideConfig[side.getIndex()] != PumpFaces.OUTPUT) { continue; }
            TileEntity tile = ICUtils.getExistingTileEntity(world, pos.offset(side));
            IFluidHandler handler = tile == null ? null : tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
            if (handler == null) { continue; }
            int i = side.getIndex();
            targets[i] = handler;
            accelerated[i] = tile instanceof IICPipe && energy.extractEnergy(ACCELERATE, true) >= ACCELERATE;
            room[i] = handler.fill(shipment(fs, fs.amount, accelerated[i]), false);
            if (room[i] > 0) {
                sum += room[i];
                last = i;
            }
        }
        int left = fs.amount;
        int moved = 0;
        for (int i = 0; i < 6 && left > 0 && sum > 0; i++) {
            if (room[i] <= 0) { continue; }
            int share = i == last ? left : (int)(fs.amount * (room[i] / (float)sum));
            boolean push = accelerated[i] && energy.extractEnergy(ACCELERATE, true) >= ACCELERATE;
            if (push) { energy.extractEnergy(ACCELERATE, false); }
            int filled = targets[i].fill(shipment(fs, share, push), true);
            moved += filled;
            left -= filled;
        }
        return moved;
    }

    private static FluidStack shipment(FluidStack source, int amount, boolean pressurized) {
        FluidStack copy = ICUtils.copyFluidStackWithAmount(source, amount, true);
        if (pressurized) { IICFluidPipe.markPressurized(copy); }
        return copy;
    }

    @Nullable private TileEntityIndustrialPump master() {
        if (!dummy) { return this; }
        TileEntity below = ICUtils.getExistingTileEntity(world, pos.down());
        return below instanceof TileEntityIndustrialPump ? (TileEntityIndustrialPump)below : null;
    }

    @Override @Nonnull public ICFluxStorage getStorage() {
        TileEntityIndustrialPump master = master();
        return master == null ? energy : master.energy;
    }

    @Override @Nonnull public ICSideConfig getSideConfig(@Nullable EnumFacing side) { return dummy && side == EnumFacing.UP ? ICSideConfig.INPUT : ICSideConfig.NONE; }

    @Override @Nullable public ICFluxWrapper getCapabilityWrapper(@Nullable EnumFacing side) {
        if (!dummy || side != EnumFacing.UP) { return null; }
        if (wrapper == null) { wrapper = new ICFluxWrapper(this, EnumFacing.UP); }
        return wrapper;
    }

    @Override public void postEnergyTransferUpdate(int moved, boolean simulate) { if (!simulate && moved != 0) { markDirty(); } }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null && !dummy && config(facing) != PumpFaces.NONE || super.hasCapability(capability, facing); }

    @SuppressWarnings("unchecked") @Override public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || facing == null || dummy || config(facing) == PumpFaces.NONE) { return super.getCapability(capability, facing); }
        int i = facing.getIndex();
        if (handlers[i] == null) { handlers[i] = new PumpFluidHandler(this, facing); }
        return (T)handlers[i];
    }

    @Override public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        int[] saved = nbt.getIntArray("sideConfig");
        sideConfig = saved.length == 6 ? saved : PumpFaces.defaults();
        dummy = nbt.getBoolean("dummy");
        active = nbt.getBoolean("active");
        tank.readFromNBT(nbt.getCompoundTag("tank"));
        energy.readFromNBT(nbt);
        if (descPacket && world != null) { world.markBlockRangeForRenderUpdate(pos, pos); }
    }

    @Override public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        nbt.setIntArray("sideConfig", sideConfig);
        nbt.setBoolean("dummy", dummy);
        if (descPacket) { nbt.setBoolean("active", active); }
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        energy.writeToNBT(nbt);
    }
}
