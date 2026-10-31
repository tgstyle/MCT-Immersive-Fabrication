package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.blocks.metal.structure.IndustrialPumpBlock;
import mctmods.immersivefabrication.common.util.FluidBlocks;
import mctmods.immersivefabrication.common.util.PumpFaces;
import mctmods.immersivefabrication.common.util.PumpSources;
import mctmods.immersivefabrication.core.IFabCommonConfig;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.block.IServerTickableBE;
import com.immersiveconvergence.api.energy.ICEnergyStorage;
import com.immersiveconvergence.api.fluid.ICPipes;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.multiblock.process.ProcessContext;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.api.util.ICFluidUtils;
import com.immersiveconvergence.api.util.MarkableFluidTank;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class IndustrialPumpBlockEntity extends BaseBlockEntity implements IServerTickableBE {
    private static final int CAPACITY = 8000;
    private static final int TANK = 4000;
    private static final int CONSUMPTION = 250;
    private static final int ACCELERATE = 5;
    private static final int PERIOD = 40;
    private static final int KEEP_PERIOD = 20;
    private static final int ACTIVE_HOLD = PERIOD + 1;
    private static final double DEGREES_PER_TICK = 72.0D;
    public static final ModelProperty<Integer> FACES = new ModelProperty<>();
    final MarkableFluidTank tank = new MarkableFluidTank(TANK, v -> setChanged());
    private final ICEnergyStorage energy = new ICEnergyStorage(CAPACITY) {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received != 0) { setChanged(); }
            return received;
        }
    };
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
    private final Map<Direction, LazyOptional<IFluidHandler>> fluidCaps = new EnumMap<>(Direction.class);
    private final PumpArea area = new PumpArea();
    private int[] sideConfig = PumpFaces.defaults();
    private boolean dummy;
    private boolean active;
    private long lastMoved = -ACTIVE_HOLD - 1;
    private float angle;
    private double spinStamp = Double.NaN;

    public IndustrialPumpBlockEntity(BlockPos pos, BlockState state) {
        super(IFabBlockEntities.INDUSTRIAL_PUMP.get(), pos, state);
        dummy = state.getValue(IndustrialPumpBlock.HALF) == DoubleBlockHalf.UPPER;
    }

    public int config(Direction side) { return sideConfig[side.get3DDataValue()]; }

    public int faceCode() { return PumpFaces.encode(sideConfig); }

    public boolean configurable(Direction side) { return !dummy && side != Direction.UP; }

    public void cycle(Direction side) {
        sideConfig[side.get3DDataValue()] = PumpFaces.next(side, config(side));
        LazyOptional<IFluidHandler> cap = fluidCaps.remove(side);
        if (cap != null) { cap.invalidate(); }
        area.clear();
        setChanged();
        markContainingBlockForUpdate(null);
    }

    public boolean isDummy() { return dummy; }

    public float spinAngle(double time) {
        if (!Double.isNaN(spinStamp) && active) { angle = (float)((angle + Math.max(0.0D, time - spinStamp) * DEGREES_PER_TICK) % 360.0D); }
        spinStamp = time;
        return angle;
    }

    @Override @Nonnull public AABB getRenderBoundingBox() { return new AABB(worldPosition, worldPosition.offset(1, 2, 1)); }

    @Override @Nonnull public ModelData getModelData() { return ModelData.builder().with(FACES, faceCode()).build(); }

    @Override public void tickServer() {
        Level world = getLevelNonnull();
        boolean moved = false;
        FluidStack held = tank.getFluid();
        if (!held.isEmpty()) {
            int sent = output(held);
            tank.drain(sent, FluidAction.EXECUTE);
            moved = sent > 0;
        }
        if (world.getBestNeighborSignal(worldPosition) > 0 || world.getBestNeighborSignal(worldPosition.above()) > 0) {
            for (Direction side : DirectionUtils.VALUES) { if (config(side) == PumpFaces.INPUT && pull(side)) { moved = true; } }
            if (pumpArea(world)) { moved = true; }
        }
        area.scan(world);
        long time = world.getGameTime();
        if (moved) { lastMoved = time; }
        boolean running = time - lastMoved <= ACTIVE_HOLD;
        if (running == active) { return; }
        active = running;
        BlockState state = getBlockState();
        world.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
    }

    private boolean pull(Direction side) {
        IFluidHandler handler = handler(neighbor(side), side);
        if (handler == null) { return false; }
        FluidStack offered = handler.drain(ICPipes.getTransferableAmount(false), FluidAction.SIMULATE);
        if (offered.isEmpty()) { return false; }
        IICFluidPipe.stripPressure(offered);
        int sent = output(offered);
        handler.drain(sent, FluidAction.EXECUTE);
        return sent > 0;
    }

    private boolean pumpArea(Level world) {
        int phase = Math.floorMod(world.getGameTime() - (worldPosition.getX() ^ worldPosition.getZ()), PERIOD);
        if (phase % KEEP_PERIOD != 0) { return false; }
        BlockPos target = area.target();
        if (target == null) {
            if (phase == 0 && !area.scanning()) { area.restart(worldPosition, sideConfig); }
            return false;
        }
        FluidStack stack = FluidBlocks.source(world, target);
        if (stack.isEmpty() || !PumpSources.pumps(stack.getFluid())) {
            area.dropTarget();
            return false;
        }
        BlockState replacement = PumpSources.replacement(stack.getFluid());
        if (replacement == null && FluidBlocks.adjacentSources(world, target, stack.getFluid()) < IFabCommonConfig.PUMP_KEEP_MIN_SOURCES.get()) {
            area.dropTarget();
            return false;
        }
        if (replacement != null && phase != 0) { return false; }
        if (tank.fill(stack, FluidAction.SIMULATE) != stack.getAmount() || energy.extractEnergy(CONSUMPTION, true) < CONSUMPTION) { return false; }
        energy.extractEnergy(CONSUMPTION, false);
        if (replacement != null) {
            FluidBlocks.take(world, target, replacement);
            area.dropTarget();
        }
        tank.fill(stack, FluidAction.EXECUTE);
        setChanged();
        return true;
    }

    private int output(FluidStack fs) {
        if (fs.getAmount() <= 0) { return 0; }
        IFluidHandler[] targets = new IFluidHandler[6];
        int[] room = new int[6];
        boolean[] accelerated = new boolean[6];
        int sum = 0;
        int last = -1;
        for (Direction side : DirectionUtils.VALUES) {
            if (config(side) != PumpFaces.OUTPUT) { continue; }
            BlockEntity neighbor = neighbor(side);
            IFluidHandler handler = handler(neighbor, side);
            if (handler == null) { continue; }
            int i = side.get3DDataValue();
            targets[i] = handler;
            accelerated[i] = IICFluidPipe.is(neighbor) && energy.extractEnergy(ACCELERATE, true) >= ACCELERATE;
            room[i] = handler.fill(shipment(fs, fs.getAmount(), accelerated[i]), FluidAction.SIMULATE);
            if (room[i] > 0) {
                sum += room[i];
                last = i;
            }
        }
        int left = fs.getAmount();
        int moved = 0;
        for (int i = 0; i < 6 && left > 0 && sum > 0; i++) {
            if (room[i] <= 0) { continue; }
            int share = i == last ? left : (int)(fs.getAmount() * (room[i] / (float)sum));
            boolean push = accelerated[i] && energy.extractEnergy(ACCELERATE, true) >= ACCELERATE;
            if (push) { energy.extractEnergy(ACCELERATE, false); }
            int filled = targets[i].fill(shipment(fs, share, push), FluidAction.EXECUTE);
            moved += filled;
            left -= filled;
        }
        return moved;
    }

    private static FluidStack shipment(FluidStack source, int amount, boolean pressurized) {
        FluidStack copy = ICFluidUtils.copyFluidStackWithAmount(source, amount, true);
        if (pressurized) { IICFluidPipe.markPressurized(copy); }
        return copy;
    }

    @Nullable private BlockEntity neighbor(Direction side) { return SafeChunkUtils.getExistingBlockEntity(getLevelNonnull(), worldPosition.relative(side)); }

    @Nullable private static IFluidHandler handler(@Nullable BlockEntity neighbor, Direction side) { return neighbor == null ? null : neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).resolve().orElse(null); }

    @Nullable private IndustrialPumpBlockEntity master() {
        if (!dummy) { return this; }
        return getLevelNonnull().getBlockEntity(worldPosition.below()) instanceof IndustrialPumpBlockEntity below ? below : null;
    }

    @Override @Nullable public IEnergyStorage getEnergyStorage() {
        IndustrialPumpBlockEntity master = master();
        return master == null ? null : master.energy;
    }

    @Override public IFluidTank[] getInternalTanks() {
        IndustrialPumpBlockEntity master = master();
        return master == null ? ProcessContext.EMPTY_TANKS : new IFluidTank[]{master.tank};
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY && dummy && side == Direction.UP) {
            IndustrialPumpBlockEntity master = master();
            return master == null ? LazyOptional.empty() : master.energyCap.cast();
        }
        if (capability != ForgeCapabilities.FLUID_HANDLER || side == null || dummy || config(side) == PumpFaces.NONE) { return super.getCapability(capability, side); }
        return fluidCaps.computeIfAbsent(side, face -> LazyOptional.of(() -> new PumpFluidHandler(this, face))).cast();
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
        fluidCaps.values().forEach(LazyOptional::invalidate);
        fluidCaps.clear();
    }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        int[] saved = nbt.getIntArray("sideConfig");
        sideConfig = saved.length == 6 ? saved : PumpFaces.defaults();
        if (nbt.contains("dummy", Tag.TAG_BYTE)) { dummy = nbt.getBoolean("dummy"); }
        active = nbt.getBoolean("active");
        tank.readFromNBT(nbt.getCompound("tank"));
        energy.setStoredEnergy(nbt.getInt("ifluxEnergy"));
        if (descPacket && level != null) {
            requestModelDataUpdate();
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        nbt.putIntArray("sideConfig", sideConfig);
        nbt.putBoolean("dummy", dummy);
        if (descPacket) { nbt.putBoolean("active", active); }
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        nbt.putInt("ifluxEnergy", energy.getEnergyStored());
    }
}
