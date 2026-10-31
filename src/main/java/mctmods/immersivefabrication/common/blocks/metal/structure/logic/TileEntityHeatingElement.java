package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.api.energy.ICFluxWrapper;
import com.immersiveconvergence.api.energy.IICInternalFluxHandler;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IActiveState;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IDirectionalTile;
import com.immersiveconvergence.api.util.ICFluxStorage;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityHeatingElement extends ICTileEntityBase implements ITickable, IICInternalFluxHandler, IActiveState, IDirectionalTile {
    private static final int CAPACITY = 32000;
    private static final int TRANSFER = 256;
    private static final int WORK_THRESHOLD = 3200;
    private static final int HEAT_ATTEMPT = 4;
    private static final int HEAT_COST = 8;
    private static final int SPEEDUP_COST = 24;
    private static final int FULL_HEAT = 200;
    private final ICFluxStorage energy = new ICFluxStorage(CAPACITY, TRANSFER);
    private EnumFacing facing = EnumFacing.NORTH;
    private boolean active;
    @Nullable private ICFluxWrapper wrapper;

    @Override public void update() {
        if (world.isRemote) { return; }
        boolean wasActive = active;
        boolean redstone = world.getRedstonePowerFromNeighbors(pos) > 0;
        if (active && !redstone) { active = false; }
        if (energy.getEnergyStored() > WORK_THRESHOLD || wasActive) {
            for (EnumFacing side : EnumFacing.VALUES) { heat(ICUtils.getExistingTileEntity(world, pos.offset(side)), redstone); }
        }
        if (active == wasActive) { return; }
        markDirty();
        markContainingBlockForUpdate(null);
    }

    private void heat(@Nullable TileEntity target, boolean redstone) {
        if (target == null) { return; }
        int available = energy.getEnergyStored();
        int consumed;
        if (ICMods.immersiveEngineering()) { consumed = ICBridges.tile().heatExternal(target, available, redstone); }
        else { consumed = target instanceof TileEntityFurnace ? heatFurnace((TileEntityFurnace)target, available, redstone) : 0; }
        if (consumed <= 0) { return; }
        energy.extractEnergy(consumed, false);
        active = true;
        markDirty();
    }

    private static int heatFurnace(TileEntityFurnace furnace, int available, boolean redstone) {
        boolean canCook = canCook(furnace);
        if (!canCook && !redstone) { return 0; }
        int consumed = 0;
        int burnTime = furnace.getField(0);
        if (burnTime < FULL_HEAT) {
            int heat = Math.min(available, HEAT_ATTEMPT * HEAT_COST) / HEAT_COST;
            if (heat > 0) {
                boolean burning = furnace.isBurning();
                furnace.setField(0, burnTime + heat);
                consumed = heat * HEAT_COST;
                if (!burning) { light(furnace); }
            }
        }
        if (canCook && furnace.getField(0) >= FULL_HEAT && furnace.getField(2) < FULL_HEAT - 1 && available - consumed > SPEEDUP_COST) {
            consumed += SPEEDUP_COST;
            furnace.setField(2, furnace.getField(2) + 1);
        }
        return consumed;
    }

    private static boolean canCook(TileEntityFurnace furnace) {
        ItemStack input = furnace.getStackInSlot(0);
        if (input.isEmpty()) { return false; }
        ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
        if (output.isEmpty()) { return false; }
        ItemStack existing = furnace.getStackInSlot(2);
        if (existing.isEmpty()) { return true; }
        int count = existing.getCount() + output.getCount();
        return existing.isItemEqual(output) && count <= furnace.getInventoryStackLimit() && count <= output.getMaxStackSize();
    }

    private static void light(TileEntityFurnace furnace) {
        World world = furnace.getWorld();
        BlockPos at = furnace.getPos();
        if (furnace.getBlockType() == Blocks.FURNACE) { BlockFurnace.setState(true, world, at); }
        NBTTagCompound nbt = furnace.writeToNBT(new NBTTagCompound());
        nbt.setBoolean("active", true);
        nbt.setBoolean("Active", true);
        furnace.readFromNBT(nbt);
        IBlockState state = world.getBlockState(at);
        world.notifyBlockUpdate(at, state, state, 3);
    }

    @Override public boolean getIsActive() { return active || world.getRedstonePowerFromNeighbors(pos) > 0; }

    @Override @Nonnull public ICFluxStorage getStorage() { return energy; }

    @Override @Nonnull public ICSideConfig getSideConfig(@Nullable EnumFacing side) { return side == facing ? ICSideConfig.INPUT : ICSideConfig.NONE; }

    @Override @Nullable public ICFluxWrapper getCapabilityWrapper(EnumFacing side) {
        if (side != facing) { return null; }
        if (wrapper == null || wrapper.side != facing) { wrapper = new ICFluxWrapper(this, facing); }
        return wrapper;
    }

    @Override public void postEnergyTransferUpdate(int moved, boolean simulate) { if (!simulate && moved != 0) { markDirty(); } }

    @Override public EnumFacing getFacing() { return facing; }

    @Override public void setFacing(EnumFacing facing) { this.facing = facing; }

    @Override public int getFacingLimitation() { return 1; }

    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return placer.isSneaking(); }

    @Override public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity) { return false; }

    @Override public boolean cannotRotate(EnumFacing axis) { return facing.getAxis() == EnumFacing.Axis.Y; }

    public void placed(EntityLivingBase placer, EnumFacing side, float hitX, float hitY, float hitZ) {
        facing = getFacingForPlacement(placer, pos, side, hitX, hitY, hitZ);
        markDirty();
    }

    public void turn(EnumFacing side, boolean sneaking) {
        EnumFacing turned = facing.rotateAround(side.getAxis());
        facing = sneaking ? turned.getOpposite() : turned;
        markDirty();
        markContainingBlockForUpdate(null);
    }

    @Override public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
        energy.readFromNBT(nbt);
        active = nbt.getBoolean("active");
        if (descPacket && world != null) { world.markBlockRangeForRenderUpdate(pos, pos); }
    }

    @Override public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        nbt.setInteger("facing", facing.getIndex());
        energy.writeToNBT(nbt);
        nbt.setBoolean("active", active);
    }
}
