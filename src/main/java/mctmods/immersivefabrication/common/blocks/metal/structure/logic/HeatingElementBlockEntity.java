package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import mctmods.immersivefabrication.common.blocks.metal.structure.HeatingElementBlock;
import mctmods.immersivefabrication.core.registration.IFabBlockEntities;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.api.block.IServerTickableBE;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.energy.ICEnergyStorage;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import javax.annotation.Nullable;

public class HeatingElementBlockEntity extends BaseBlockEntity implements IServerTickableBE {
    private static final int CAPACITY = 32000;
    private static final int TRANSFER = 256;
    private static final int WORK_THRESHOLD = 3200;
    private static final int HEAT_ATTEMPT = 4;
    private static final int HEAT_COST = 8;
    private static final int SPEEDUP_COST = 24;
    private static final int FULL_HEAT = 200;
    private final ICEnergyStorage energy = new ICEnergyStorage(CAPACITY, TRANSFER) {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received != 0) { setChanged(); }
            return received;
        }
    };
    private Direction facing;
    private boolean active;

    public HeatingElementBlockEntity(BlockPos pos, BlockState state) {
        super(IFabBlockEntities.HEATING_ELEMENT.get(), pos, state);
        facing = state.getValue(HeatingElementBlock.FACING);
    }

    @Override public void tickServer() {
        Level world = getLevelNonnull();
        boolean wasActive = active;
        boolean redstone = world.getBestNeighborSignal(worldPosition) > 0;
        if (active && !redstone) { active = false; }
        if (energy.getEnergyStored() > WORK_THRESHOLD || wasActive) {
            for (Direction side : DirectionUtils.VALUES) { heat(SafeChunkUtils.getExistingBlockEntity(world, worldPosition.relative(side)), redstone); }
        }
        if (active != wasActive) { setChanged(); }
        syncState(redstone);
    }

    private void heat(@Nullable BlockEntity target, boolean redstone) {
        if (target == null) { return; }
        int available = energy.getEnergyStored();
        int consumed;
        if (ICMods.immersiveEngineering()) { consumed = ICBridges.integration().heatExternal(target, available, redstone); }
        else { consumed = target instanceof FurnaceBlockEntity furnace ? heatFurnace(furnace, available, redstone) : 0; }
        if (consumed <= 0) { return; }
        energy.extractEnergy(consumed, false);
        active = true;
        setChanged();
    }

    private static int heatFurnace(FurnaceBlockEntity furnace, int available, boolean redstone) {
        boolean canCook = canCook(furnace);
        if (!canCook && !redstone) { return 0; }
        int consumed = 0;
        if (furnace.litTime < FULL_HEAT) {
            int heat = Math.min(available, HEAT_ATTEMPT * HEAT_COST) / HEAT_COST;
            if (heat > 0) {
                furnace.litTime += heat;
                consumed = heat * HEAT_COST;
                light(furnace);
            }
        }
        if (canCook && furnace.litTime >= FULL_HEAT && furnace.cookingProgress < FULL_HEAT - 1 && available - consumed > SPEEDUP_COST) {
            consumed += SPEEDUP_COST;
            furnace.cookingProgress++;
        }
        return consumed;
    }

    private static boolean canCook(FurnaceBlockEntity furnace) {
        Level world = furnace.getLevel();
        ItemStack input = furnace.getItem(0);
        if (world == null || input.isEmpty()) { return false; }
        ItemStack output = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), world).map(holder -> holder.value().getResultItem(world.registryAccess())).orElse(ItemStack.EMPTY);
        if (output.isEmpty()) { return false; }
        ItemStack existing = furnace.getItem(2);
        if (existing.isEmpty()) { return true; }
        int count = existing.getCount() + output.getCount();
        return ItemStack.isSameItem(existing, output) && count <= furnace.getMaxStackSize() && count <= output.getMaxStackSize();
    }

    private static void light(FurnaceBlockEntity furnace) {
        Level world = furnace.getLevel();
        BlockState state = furnace.getBlockState();
        if (world != null && !state.getValue(AbstractFurnaceBlock.LIT)) { world.setBlock(furnace.getBlockPos(), state.setValue(AbstractFurnaceBlock.LIT, true), Block.UPDATE_ALL); }
    }

    private void syncState(boolean redstone) {
        BlockState state = getBlockState();
        BlockState synced = state.setValue(HeatingElementBlock.FACING, facing).setValue(HeatingElementBlock.ACTIVE, active || redstone);
        if (synced != state) { getLevelNonnull().setBlock(worldPosition, synced, Block.UPDATE_ALL); }
    }

    public void turn(Direction side, boolean sneaking) {
        Level world = getLevelNonnull();
        Direction turned = facing.getClockWise(side.getAxis());
        facing = sneaking ? turned.getOpposite() : turned;
        world.invalidateCapabilities(worldPosition);
        setChanged();
        syncState(world.getBestNeighborSignal(worldPosition) > 0);
    }

    @Override public IEnergyStorage getEnergyStorage() { return energy; }

    @Nullable public IEnergyStorage energyHandler(@Nullable Direction side) { return side == facing ? energy : null; }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        if (nbt.contains("facing", Tag.TAG_INT)) { facing = Direction.from3DDataValue(nbt.getInt("facing")); }
        energy.setStoredEnergy(nbt.getInt("energy"));
        active = nbt.getBoolean("active");
    }

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        nbt.putInt("facing", facing.get3DDataValue());
        nbt.putInt("energy", energy.getEnergyStored());
        nbt.putBoolean("active", active);
    }
}
