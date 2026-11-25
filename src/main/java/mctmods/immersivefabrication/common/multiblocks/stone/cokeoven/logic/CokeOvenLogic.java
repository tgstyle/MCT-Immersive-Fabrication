package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic;

import mctmods.immersivefabrication.common.multiblocks.IFabShapes;
import mctmods.immersivefabrication.common.multiblocks.MultiblockGui;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenCooking;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenDoor;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenFluids;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenInput;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenOutput;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRules;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenStacks;
import mctmods.immersivefabrication.core.registration.IFabMenuTypes;

import com.immersiveconvergence.api.multiblock.IDataReloadAware;
import com.immersiveconvergence.api.multiblock.MultiblockPOIHelper;
import com.immersiveconvergence.api.multiblock.PoIJSONSchema;
import com.immersiveconvergence.api.multiblock.blocks.component.IClientTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IServerTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockLogic;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;
import com.immersiveconvergence.api.multiblock.blocks.util.StoredCapability;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;
import com.immersiveconvergence.api.util.MarkableFluidTank;
import com.immersiveconvergence.api.util.MultiTankFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CokeOvenLogic implements IMultiblockLogic<CokeOvenLogic.State>, IServerTickableComponent<CokeOvenLogic.State>, IClientTickableComponent<CokeOvenLogic.State> {
    public static BlockPos DOOR_CELL;
    public static BlockPos CHAMBER_CELL;
    public static MultiblockFace RCOG_PORT;
    public static MultiblockFace FLUE_PORT;
    public static RelativeBlockFace INPUT_FACE;
    public static List<BlockPos> INPUT_CELLS;
    public static List<MultiblockFace> OUTPUT_CELLS;
    private static final MultiblockGui<State> GUI = new MultiblockGui<>(IFabMenuTypes.COKE_OVEN);

    static { IFabShapes.readPois("coke_oven", CokeOvenLogic::loadPois); }

    @Override public void tickClient(IMultiblockContext<State> ctx) {
        Level level = ctx.getLevel().getRawLevel();
        if (ctx.getState().fire <= 0 || level.getGameTime() % 3 != 0) { return; }
        BlockPos chamber = ctx.getLevel().toAbsolute(CHAMBER_CELL);
        RandomSource random = level.random;
        level.addParticle(ParticleTypes.FLAME, chamber.getX() + 0.3 + random.nextDouble() * 0.4, chamber.getY() + 0.2 + random.nextDouble() * 0.4, chamber.getZ() + 0.3 + random.nextDouble() * 0.4, 0.0, 0.02, 0.0);
    }

    @Override public void tickServer(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        CokeOvenCooking cooking = state.cooking;
        boolean wasActive = state.isActive();
        boolean wasPaused = cooking.paused();
        boolean wanted = CokeOvenStacks.isCoalBlock(state.chamber) && !state.doorOpen;
        int[] burnTimes = new int[2];
        for (int i = 0; i < 2; i++) { burnTimes[i] = burnTime(state.fuel.get(i)); }
        Fluid flueFluid = CokeOvenFluids.flueGas();
        boolean flueExists = flueFluid != Fluids.EMPTY;
        CokeOvenCooking.Result result = cooking.tick(wanted, burnTimes, state.rcog.getFluidAmount(), state.flue.getFluidAmount(), flueExists);
        boolean changed = result.finished;
        for (int i = 0; i < 2; i++) {
            if (result.consume[i]) {
                state.consumeFuel(i);
                changed = true;
            }
        }
        if (result.gas > 0) { state.rcog.fill(new FluidStack(CokeOvenFluids.rawGas(), result.gas), FluidAction.EXECUTE); }
        if (result.flue > 0 && flueExists) { state.flue.fill(new FluidStack(flueFluid, result.flue), FluidAction.EXECUTE); }
        if (result.finished) { state.chamber = CokeOvenStacks.cokeBlock(); }
        long time = ctx.getLevel().getRawLevel().getGameTime();
        boolean pushed = time % 8 == 0 && CokeOvenOutput.pushCoke(state);
        changed |= pushed;
        CokeOvenOutput.pushFluid(state.rcogTarget, state.rcog);
        CokeOvenOutput.pushFluid(state.flueTarget, state.flue);
        if (state.fire > 0) {
            state.fire--;
            changed = true;
        }
        boolean active = state.isActive();
        if (active != wasActive || cooking.paused() != wasPaused || result.finished || pushed || active && time % 8 == 0) { ctx.requestMasterBESync(); }
        if (changed || active) { ctx.markMasterDirty(); }
    }

    private static int burnTime(ItemStack stack) { return stack.isEmpty() ? 0 : Math.max(CokeOvenStacks.burnTime(stack), 0); }

    @Override public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap) {
        State state = ctx.getState();
        if (cap == ForgeCapabilities.ITEM_HANDLER && position.side() == INPUT_FACE && INPUT_CELLS.contains(position.posInMultiblock())) { return state.inputCap.cast(ctx); }
        if (cap == ForgeCapabilities.FLUID_HANDLER && at(RCOG_PORT, position)) { return state.rcogCap.cast(ctx); }
        if (cap == ForgeCapabilities.FLUID_HANDLER && at(FLUE_PORT, position)) { return state.flueCap.cast(ctx); }
        return LazyOptional.empty();
    }

    private static boolean at(MultiblockFace poi, CapabilityPosition position) { return poi.face() == position.side() && poi.posInMultiblock().equals(position.posInMultiblock()); }

    @Override public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
        InteractionResult door = CokeOvenDoor.use(ctx, posInMultiblock, player, hand, absoluteHit.getDirection(), isClient);
        if (door != InteractionResult.PASS) { return door; }
        return GUI.open(ctx, posInMultiblock, player, hand, isClient);
    }

    @Override public void dropExtraItems(State state, Consumer<ItemStack> drop) {
        for (ItemStack stack : state.fuel) { if (!stack.isEmpty()) { drop.accept(stack.copy()); } }
        if (!state.chamber.isEmpty()) { drop.accept(state.chamber.copy()); }
        state.fuel.clear();
        state.chamber = ItemStack.EMPTY;
    }

    @Override public State createInitialState(IInitialMultiblockContext<State> ctx) { return new State(ctx); }

    @Override public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) { return IFabShapes.get("coke_oven").getter; }

    private static void loadPois(List<PoIJSONSchema> pois) {
        DOOR_CELL = MultiblockPOIHelper.getPosList(pois, "trigger").get(0);
        CHAMBER_CELL = RelativeBlockFace.FRONT.offsetRelative(MultiblockPOIHelper.getPosList(pois, "master").get(0), 1);
        RCOG_PORT = port(pois, "fluid_output0");
        FLUE_PORT = port(pois, "fluid_output1");
        INPUT_FACE = MultiblockPOIHelper.getFacing(pois, "item_input0");
        INPUT_CELLS = MultiblockPOIHelper.getPosList(pois, "item_input0");
        RelativeBlockFace outputFace = MultiblockPOIHelper.getFacing(pois, "item_output0");
        OUTPUT_CELLS = MultiblockPOIHelper.getPosList(pois, "item_output0").stream().sorted(Comparator.<BlockPos>comparingInt(BlockPos::getX).thenComparingInt(cell -> -cell.getZ())).map(cell -> new MultiblockFace(outputFace, cell)).toList();
    }

    private static MultiblockFace port(List<PoIJSONSchema> pois, String name) { return new MultiblockFace(MultiblockPOIHelper.getFacing(pois, name), MultiblockPOIHelper.getPosList(pois, name).get(0)); }

    public static class State implements IMultiblockState, IDataReloadAware {
        private final Supplier<Level> level;
        private final Runnable markDirty;
        private final Runnable sync;
        private ItemStack chamber = ItemStack.EMPTY;
        private final NonNullList<ItemStack> fuel = NonNullList.withSize(2, ItemStack.EMPTY);
        private final MarkableFluidTank rcog;
        private final MarkableFluidTank flue;
        private boolean doorOpen;
        private long doorTime;
        private int fire;
        private int fuelNext;
        private final CokeOvenCooking cooking = new CokeOvenCooking();
        private final StoredCapability<IItemHandler> inputCap;
        private final StoredCapability<IFluidHandler> rcogCap;
        private final StoredCapability<IFluidHandler> flueCap;
        private List<CapabilityReference<IItemHandler>> cokeTargets;
        private CapabilityReference<IFluidHandler> rcogTarget;
        private CapabilityReference<IFluidHandler> flueTarget;

        public State(IInitialMultiblockContext<State> ctx) {
            this.level = ctx.levelSupplier();
            this.markDirty = ctx.getMarkDirtyRunnable();
            this.sync = ctx.getSyncRunnable();
            this.rcog = new MarkableFluidTank(CokeOvenRules.TANK, v -> changed());
            this.flue = new MarkableFluidTank(CokeOvenRules.TANK, v -> changed());
            this.inputCap = new StoredCapability<>(new CokeOvenInput(this));
            this.rcogCap = new StoredCapability<>(MultiTankFluidHandler.drainOnly(rcog, null));
            this.flueCap = new StoredCapability<>(MultiTankFluidHandler.drainOnly(flue, null));
            bind(ctx);
        }

        @Override public void onDataReload(IInitialMultiblockContext<?> context) { bind(context); }

        private void bind(IInitialMultiblockContext<?> ctx) {
            this.cokeTargets = OUTPUT_CELLS.stream().map(cell -> ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, MultiblockPOIHelper.opposing(cell.face(), cell.posInMultiblock()))).toList();
            this.rcogTarget = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, MultiblockPOIHelper.opposing(RCOG_PORT.face(), RCOG_PORT.posInMultiblock()));
            this.flueTarget = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, MultiblockPOIHelper.opposing(FLUE_PORT.face(), FLUE_PORT.posInMultiblock()));
        }

        public void changed() {
            Level world = level.get();
            if (world == null || world.isClientSide) { return; }
            markDirty.run();
            sync.run();
        }

        private void consumeFuel(int slot) {
            ItemStack stack = fuel.get(slot);
            ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            stack.shrink(1);
            if (stack.isEmpty()) { fuel.set(slot, container); }
        }

        @Override public void writeSaveNBT(CompoundTag nbt) {
            nbt.put("chamber", chamber.isEmpty() ? new CompoundTag() : chamber.save(new CompoundTag()));
            nbt.put("fuel", ContainerHelper.saveAllItems(new CompoundTag(), fuel).getList("Items", Tag.TAG_COMPOUND));
            cooking.writeNBT(nbt);
            nbt.putInt("fuelNext", fuelNext);
            nbt.putBoolean("doorOpen", doorOpen);
            nbt.putLong("doorTime", doorTime);
            nbt.putInt("fire", fire);
            nbt.put("rcog", rcog.writeToNBT(new CompoundTag()));
            nbt.put("flue", flue.writeToNBT(new CompoundTag()));
        }

        @Override public void readSaveNBT(CompoundTag nbt) {
            CompoundTag chamberTag = nbt.getCompound("chamber");
            chamber = chamberTag.isEmpty() ? ItemStack.EMPTY : ItemStack.of(chamberTag);
            CompoundTag fuelTag = new CompoundTag();
            fuelTag.put("Items", nbt.getList("fuel", Tag.TAG_COMPOUND));
            fuel.clear();
            ContainerHelper.loadAllItems(fuelTag, fuel);
            cooking.readNBT(nbt);
            fuelNext = nbt.getInt("fuelNext");
            doorOpen = nbt.getBoolean("doorOpen");
            doorTime = nbt.getLong("doorTime");
            fire = nbt.getInt("fire");
            rcog.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("rcog")));
            flue.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("flue")));
        }

        @Override public void writeSyncNBT(CompoundTag nbt) { writeSaveNBT(nbt); }

        @Override public void readSyncNBT(CompoundTag nbt) { readSaveNBT(nbt); }

        public boolean isActive() { return cooking.cooking() && !cooking.paused(); }

        public ItemStack getChamber() { return chamber; }

        public void setChamber(ItemStack stack) { chamber = stack; }

        public NonNullList<ItemStack> getInventory() { return fuel; }

        public int getFuelNext() { return fuelNext; }

        public void setFuelNext(int slot) { fuelNext = slot; }

        public boolean isDoorOpen() { return doorOpen; }

        public void setDoor(boolean open, long time) {
            doorOpen = open;
            doorTime = time;
        }

        public long getDoorTime() { return doorTime; }

        public int getFire() { return fire; }

        public void setFire(int ticks) { fire = ticks; }

        public CokeOvenCooking getCooking() { return cooking; }

        public MarkableFluidTank getRcog() { return rcog; }

        public MarkableFluidTank getFlue() { return flue; }

        public List<CapabilityReference<IItemHandler>> getCokeTargets() { return cokeTargets; }
    }
}
