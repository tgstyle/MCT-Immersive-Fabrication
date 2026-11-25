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

import com.immersiveconvergence.api.multiblock.MultiblockPOIHelper;
import com.immersiveconvergence.api.multiblock.PoIJSONSchema;
import com.immersiveconvergence.api.multiblock.blocks.component.IClientTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IServerTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockLogic;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;
import com.immersiveconvergence.api.util.MarkableFluidTank;
import com.immersiveconvergence.api.util.MultiTankFluidHandler;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CokeOvenLogic implements IMultiblockLogic<CokeOvenLogic.State>, IServerTickableComponent<CokeOvenLogic.State>, IClientTickableComponent<CokeOvenLogic.State> {
    public static BlockPos DOOR_POI;
    private static BlockPos MASTER_POI;
    private static CapabilityPosition FLUID_OUTPUT0_POI;
    private static CapabilityPosition FLUID_OUTPUT1_POI;
    private static List<CapabilityPosition> ITEM_INPUT_POIS;
    private static List<BlockPos> ITEM_OUTPUT_POIS;

    private static final MultiblockGui<State> GUI = new MultiblockGui<>(IFabMenuTypes.COKE_OVEN);

    static { IFabShapes.readPois("coke_oven", CokeOvenLogic::loadPois); }

    @Override public State createInitialState(IInitialMultiblockContext<State> ctx) { return new State(ctx); }

    @Override public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) { return IFabShapes.get("coke_oven").getter; }

    @Override public void tickClient(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        IMultiblockLevel mbLevel = ctx.getLevel();
        Level level = mbLevel.getRawLevel();
        if (state.fire <= 0 || level.getGameTime() % 3 != 0) { return; }
        BlockPos chamber = mbLevel.toAbsolute(MASTER_POI).relative(mbLevel.getOrientation().front());
        level.addParticle(ParticleTypes.FLAME, chamber.getX() + 0.3 + level.random.nextDouble() * 0.4, chamber.getY() + 0.2 + level.random.nextDouble() * 0.4, chamber.getZ() + 0.3 + level.random.nextDouble() * 0.4, 0.0, 0.02, 0.0);
    }

    @Override public void tickServer(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        IMultiblockLevel mbLevel = ctx.getLevel();
        Level level = mbLevel.getRawLevel();
        boolean wasActive = state.isActive();
        boolean wasPaused = state.cooking.paused();
        boolean wanted = CokeOvenStacks.isCoalBlock(state.chamber) && !state.doorOpen;
        int[] burnTimes = new int[2];
        for (int i = 0; i < 2; i++) { burnTimes[i] = CokeOvenStacks.burnTime(state.fuel.get(i)); }
        Fluid flueFluid = CokeOvenFluids.flueGas();
        CokeOvenCooking.Result result = state.cooking.tick(wanted, burnTimes, state.rcog.getFluidAmount(), state.flue.getFluidAmount(), flueFluid != Fluids.EMPTY);
        boolean changed = result.finished;
        for (int i = 0; i < 2; i++) {
            if (result.consume[i]) {
                state.consumeFuel(i);
                changed = true;
            }
        }
        if (result.gas > 0) { state.rcog.fill(new FluidStack(CokeOvenFluids.rawGas(), result.gas), FluidAction.EXECUTE); }
        if (result.flue > 0 && flueFluid != Fluids.EMPTY) { state.flue.fill(new FluidStack(flueFluid, result.flue), FluidAction.EXECUTE); }
        if (result.finished) { state.chamber = CokeOvenStacks.cokeBlock(); }
        boolean pushed = level.getGameTime() % 8 == 0 && CokeOvenOutput.pushCoke(mbLevel, state, ITEM_OUTPUT_POIS);
        changed |= pushed;
        CokeOvenOutput.pushFluid(level, mbLevel.toAbsolute(FLUID_OUTPUT0_POI.posInMultiblock()), state.rcog);
        CokeOvenOutput.pushFluid(level, mbLevel.toAbsolute(FLUID_OUTPUT1_POI.posInMultiblock()), state.flue);
        if (state.fire > 0) {
            state.fire--;
            changed = true;
        }
        boolean active = state.isActive();
        if (active != wasActive || state.cooking.paused() != wasPaused || result.finished || pushed || active && level.getGameTime() % 8 == 0) { ctx.requestMasterBESync(); }
        if (changed || active) { ctx.markMasterDirty(); }
    }

    @Override public void registerCapabilities(IMultiblockComponent.CapabilityRegistrar<State> register) {
        register.register(Capabilities.ItemHandler.BLOCK, (state, position) -> ITEM_INPUT_POIS.contains(position) ? state.input : null);
        register.register(Capabilities.FluidHandler.BLOCK, (state, position) -> {
            if (FLUID_OUTPUT0_POI.equals(position)) { return state.rcogCap; }
            return FLUID_OUTPUT1_POI.equals(position) ? state.flueCap : null;
        });
    }

    @Override public ItemInteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
        if (CokeOvenDoor.use(ctx, posInMultiblock, player, hand, absoluteHit.getDirection(), isClient)) { return ItemInteractionResult.SUCCESS; }
        return GUI.open(ctx, posInMultiblock, player, hand, isClient);
    }

    @Override public void dropExtraItems(State state, Consumer<ItemStack> drop) { state.dropContents(drop); }

    private static void loadPois(List<PoIJSONSchema> pois) {
        MASTER_POI = MultiblockPOIHelper.getPosList(pois, "master").getFirst();
        DOOR_POI = MultiblockPOIHelper.getPosList(pois, "trigger").getFirst();
        FLUID_OUTPUT0_POI = MultiblockPOIHelper.getCapabilityPosition(pois, "fluid_output0");
        FLUID_OUTPUT1_POI = MultiblockPOIHelper.getCapabilityPosition(pois, "fluid_output1");
        ITEM_INPUT_POIS = MultiblockPOIHelper.getCapabilityPositions(pois, "item_input0");
        ITEM_OUTPUT_POIS = MultiblockPOIHelper.getPosList(pois, "item_output0").stream().sorted(Comparator.<BlockPos>comparingInt(BlockPos::getX).thenComparingInt(cell -> -cell.getZ())).toList();
    }

    public static class State implements IMultiblockState {
        private ItemStack chamber = ItemStack.EMPTY;
        private final NonNullList<ItemStack> fuel = NonNullList.withSize(2, ItemStack.EMPTY);
        private final MarkableFluidTank rcog;
        private final MarkableFluidTank flue;
        private final IFluidHandler rcogCap;
        private final IFluidHandler flueCap;
        private final CokeOvenInput input;
        private final CokeOvenCooking cooking = new CokeOvenCooking();
        private final Runnable onChanged;
        private boolean doorOpen;
        private long doorTime;
        private int fire;
        private int fuelNext;

        public State(IInitialMultiblockContext<State> ctx) {
            Runnable markDirty = ctx.getMarkDirtyRunnable();
            Runnable sync = ctx.getSyncRunnable();
            Runnable changed = () -> {
                markDirty.run();
                sync.run();
            };
            this.onChanged = changed;
            this.rcog = new MarkableFluidTank(CokeOvenRules.TANK, v -> changed.run());
            this.flue = new MarkableFluidTank(CokeOvenRules.TANK, v -> changed.run());
            this.rcogCap = new MultiTankFluidHandler(rcog, true, false, null);
            this.flueCap = new MultiTankFluidHandler(flue, true, false, null);
            this.input = new CokeOvenInput(this);
        }

        public boolean isActive() { return cooking.cooking() && !cooking.paused(); }

        public ItemStack getChamber() { return chamber; }

        public void setChamber(ItemStack stack) { chamber = stack; }

        public NonNullList<ItemStack> getFuel() { return fuel; }

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

        public void markChanged() { onChanged.run(); }

        private void consumeFuel(int slot) {
            ItemStack stack = fuel.get(slot);
            ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            stack.shrink(1);
            if (stack.isEmpty()) { fuel.set(slot, container); }
        }

        private void dropContents(Consumer<ItemStack> drop) {
            for (ItemStack stack : fuel) {
                if (!stack.isEmpty()) { drop.accept(stack.copy()); }
            }
            if (!chamber.isEmpty()) { drop.accept(chamber.copy()); }
            fuel.clear();
            chamber = ItemStack.EMPTY;
        }

        @Override public void writeSaveNBT(CompoundTag nbt, Provider provider) { write(nbt, provider); }

        @Override public void readSaveNBT(CompoundTag nbt, Provider provider) { read(nbt, provider); }

        @Override public void writeSyncNBT(CompoundTag nbt, Provider provider) { write(nbt, provider); }

        @Override public void readSyncNBT(CompoundTag nbt, Provider provider) { read(nbt, provider); }

        private void write(CompoundTag nbt, Provider provider) {
            nbt.put("chamber", chamber.saveOptional(provider));
            nbt.put("fuel", writeFuel(provider));
            cooking.writeNBT(nbt);
            nbt.putInt("fuelNext", fuelNext);
            nbt.putBoolean("doorOpen", doorOpen);
            nbt.putLong("doorTime", doorTime);
            nbt.putInt("fire", fire);
            nbt.put("rcog", rcog.getFluid().saveOptional(provider));
            nbt.put("flue", flue.getFluid().saveOptional(provider));
        }

        private void read(CompoundTag nbt, Provider provider) {
            chamber = ItemStack.parseOptional(provider, nbt.getCompound("chamber"));
            readFuel(nbt.getList("fuel", Tag.TAG_COMPOUND), provider);
            cooking.readNBT(nbt);
            fuelNext = nbt.getInt("fuelNext");
            doorOpen = nbt.getBoolean("doorOpen");
            doorTime = nbt.getLong("doorTime");
            fire = nbt.getInt("fire");
            rcog.setFluid(FluidStack.parseOptional(provider, nbt.getCompound("rcog")));
            flue.setFluid(FluidStack.parseOptional(provider, nbt.getCompound("flue")));
        }

        private ListTag writeFuel(Provider provider) {
            ListTag list = new ListTag();
            for (int i = 0; i < fuel.size(); i++) {
                ItemStack stack = fuel.get(i);
                if (stack.isEmpty()) { continue; }
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                list.add(stack.save(provider, itemTag));
            }
            return list;
        }

        private void readFuel(ListTag list, Provider provider) {
            fuel.clear();
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < fuel.size()) { fuel.set(slot, ItemStack.parseOptional(provider, itemTag)); }
            }
        }
    }
}
