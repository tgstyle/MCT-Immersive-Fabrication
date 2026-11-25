package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenCooking;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenFluids;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenInput;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenOutput;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenStacks;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces;
import com.immersiveconvergence.api.network.ITileSyncReceiver;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.api.util.ICFluidTank;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.api.util.IICInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCokeOvenMaster extends TileEntityCokeOvenSlave implements IICInventory, ITileSyncReceiver, ICBlockInterfaces.IActiveState, ICFluidTank.TankListener {
    private ItemStack chamber = ItemStack.EMPTY;
    private NonNullList<ItemStack> fuel = NonNullList.withSize(2, ItemStack.EMPTY);
    private final ICFluidTank rcog = new ICFluidTank(CokeOvenRules.TANK, this);
    private final ICFluidTank flue = new ICFluidTank(CokeOvenRules.TANK, this);
    private boolean doorOpen;
    private long doorTime;
    private int fire;
    private int fuelNext;
    private final CokeOvenCooking cooking = new CokeOvenCooking();
    private final CokeOvenInput input = new CokeOvenInput(this);
    private final IFluidTank[] internalTanks = {rcog, flue};

    @Override public boolean isDummy() { return false; }

    @Override @Nonnull public IFluidTank[] getInternalTanks() { return internalTanks; }

    @Override @Nonnull protected IFluidTank[] getAccessibleFluidTanks(@Nullable EnumFacing side, BlockPos position) {
        if (!formed) { return NO_TANKS; }
        if (isPoI("fluid_output0", side, position)) { return tankView(0, rcog); }
        if (isPoI("fluid_output1", side, position)) { return tankView(1, flue); }
        return NO_TANKS;
    }

    @Override protected boolean canDrainTankFrom(int iTank, @Nonnull EnumFacing side, BlockPos position) {
        return iTank == 0 && isPoI("fluid_output0", side, position) || iTank == 1 && isPoI("fluid_output1", side, position);
    }

    public CokeOvenInput getInput() { return input; }

    @Override public TileEntityCokeOvenMaster master() { return this; }

    @Override public void update() {
        if (!formed) { return; }
        super.update();
        if (world.isRemote) {
            if (fire > 0 && world.getTotalWorldTime() % 3 == 0) {
                BlockPos chamberPos = getPos().offset(getFacing());
                world.spawnParticle(EnumParticleTypes.FLAME, chamberPos.getX() + 0.3 + world.rand.nextDouble() * 0.4, chamberPos.getY() + 0.2 + world.rand.nextDouble() * 0.4, chamberPos.getZ() + 0.3 + world.rand.nextDouble() * 0.4, 0.0, 0.02, 0.0);
            }
            return;
        }
        boolean wasActive = getIsActive();
        boolean wasPaused = cooking.paused();
        boolean wanted = CokeOvenStacks.isCoalBlock(chamber) && !doorOpen;
        int[] burnTimes = new int[2];
        for (int i = 0; i < 2; i++) { burnTimes[i] = burnTime(fuel.get(i)); }
        Fluid flueFluid = CokeOvenFluids.flueGas();
        CokeOvenCooking.Result result = cooking.tick(wanted, burnTimes, rcog.getFluidAmount(), flue.getFluidAmount(), flueFluid != null);
        boolean changed = result.finished;
        for (int i = 0; i < 2; i++) {
            if (result.consume[i]) {
                consumeFuel(i);
                changed = true;
            }
        }
        if (result.gas > 0) { rcog.fill(new FluidStack(CokeOvenFluids.rawGas(), result.gas), true); }
        if (result.flue > 0 && flueFluid != null) { flue.fill(new FluidStack(flueFluid, result.flue), true); }
        if (result.finished) { chamber = CokeOvenStacks.cokeBlock(); }
        boolean pushed = world.getTotalWorldTime() % 8 == 0 && CokeOvenOutput.pushCoke(world, this);
        changed |= pushed;
        CokeOvenOutput.pushFluid(world, poiWorldPos("fluid_output0"), rcog);
        CokeOvenOutput.pushFluid(world, poiWorldPos("fluid_output1"), flue);
        if (fire > 0) {
            fire--;
            changed = true;
        }
        boolean active = getIsActive();
        if (active != wasActive || cooking.paused() != wasPaused) {
            markContainingBlockForUpdate(null);
            notifyNearbyClients();
        }
        else if (result.finished || pushed || active && world.getTotalWorldTime() % 8 == 0) { notifyNearbyClients(); }
        if (changed || active) { world.getChunk(getPos()).markDirty(); }
    }

    private static int burnTime(ItemStack stack) {
        if (stack.isEmpty()) { return 0; }
        int time = ForgeEventFactory.getItemBurnTime(stack);
        if (time < 0) { time = TileEntityFurnace.getItemBurnTime(stack); }
        return Math.max(time, 0);
    }

    private void consumeFuel(int slot) {
        ItemStack stack = fuel.get(slot);
        ItemStack container = stack.getItem().hasContainerItem(stack) ? stack.getItem().getContainerItem(stack) : ItemStack.EMPTY;
        stack.shrink(1);
        if (stack.isEmpty()) { fuel.set(slot, container); }
    }

    private void writeOwn(NBTTagCompound nbt) {
        nbt.setTag("chamber", chamber.isEmpty() ? new NBTTagCompound() : chamber.writeToNBT(new NBTTagCompound()));
        nbt.setTag("fuel", ICUtils.writeInventory(fuel));
        cooking.writeNBT(nbt);
        nbt.setInteger("fuelNext", fuelNext);
        nbt.setBoolean("doorOpen", doorOpen);
        nbt.setLong("doorTime", doorTime);
        nbt.setInteger("fire", fire);
        nbt.setTag("rcog", rcog.writeToNBT(new NBTTagCompound()));
        nbt.setTag("flue", flue.writeToNBT(new NBTTagCompound()));
    }

    private void readOwn(NBTTagCompound nbt) {
        NBTTagCompound chamberTag = nbt.getCompoundTag("chamber");
        chamber = chamberTag.isEmpty() ? ItemStack.EMPTY : new ItemStack(chamberTag);
        fuel = ICUtils.readInventory(nbt.getTagList("fuel", 10), 2);
        cooking.readNBT(nbt);
        fuelNext = nbt.getInteger("fuelNext");
        doorOpen = nbt.getBoolean("doorOpen");
        doorTime = nbt.getLong("doorTime");
        fire = nbt.getInteger("fire");
        rcog.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("rcog")));
        flue.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("flue")));
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        writeOwn(nbt);
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        readOwn(nbt);
    }

    private void notifyNearbyClients() {
        NBTTagCompound tag = new NBTTagCompound();
        writeOwn(tag);
        BlockPos center = getPos();
        ImmersiveConvergence.packetHandler.sendToAllTracking(new TileSyncMessage(this, tag), new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 0));
    }

    @Override public void receiveMessageFromServer(NBTTagCompound message) { readOwn(message); }

    @Override public void TankContentsChanged() {
        if (world == null || world.isRemote) { return; }
        world.getChunk(getPos()).markDirty();
        requestClientSync();
    }

    @Override public void disassemble() {
        if (!world.isRemote) {
            for (ItemStack stack : fuel) { dropStack(stack); }
            dropStack(chamber);
            fuel.clear();
            chamber = ItemStack.EMPTY;
        }
        super.disassemble();
    }

    private void dropStack(ItemStack stack) {
        if (stack.isEmpty()) { return; }
        world.spawnEntity(new EntityItem(world, getPos().getX() + .5, getPos().getY() + .5, getPos().getZ() + .5, stack.copy()));
    }

    @Override public NonNullList<ItemStack> getInventory() { return fuel; }

    @Override public boolean isStackValid(int slot, ItemStack stack) { return CokeOvenStacks.isFuel(stack); }

    @Override public void doGraphicalUpdates(int slot) {
        world.getChunk(getPos()).markDirty();
        markContainingBlockForUpdate(null);
    }

    @Override public boolean getIsActive() { return cooking.cooking() && !cooking.paused(); }

    public ItemStack getChamber() { return chamber; }

    public void setChamber(ItemStack stack) { chamber = stack; }

    public boolean isDoorOpen() { return doorOpen; }

    public long getDoorTime() { return doorTime; }

    public void setDoor(boolean open, long time) {
        doorOpen = open;
        doorTime = time;
    }

    public int getFire() { return fire; }

    public void setFire(int ticks) { fire = ticks; }

    public void sync() {
        world.getChunk(getPos()).markDirty();
        markContainingBlockForUpdate(null);
        notifyNearbyClients();
    }

    public int getFuelNext() { return fuelNext; }

    public void setFuelNext(int slot) { fuelNext = slot; }

    public ICFluidTank getRcog() { return rcog; }

    public ICFluidTank getFlue() { return flue; }

    public CokeOvenCooking getCooking() { return cooking; }
}
