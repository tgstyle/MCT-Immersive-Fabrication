package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic;

import mctmods.immersivefabrication.common.gui.IFabGuiIds;
import mctmods.immersivefabrication.common.multiblocks.IFabShapes;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenDoor;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenMultiblock;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.CokeOvenRecipe;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.GenericShape;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGuiTile;
import com.immersiveconvergence.api.multiblock.ICTickingRegistry;
import com.immersiveconvergence.api.multiblock.TileEntityTemplateMultiblock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCokeOvenSlave extends TileEntityTemplateMultiblock<TileEntityCokeOvenSlave, ICMultiblockRecipe, TileEntityCokeOvenMaster> implements IGuiTile {
    protected static final IFluidTank[] NO_TANKS = new IFluidTank[0];
    private static final int[] NO_OUTPUT_TANKS = new int[0];
    private int loadGrace = 0;

    public TileEntityCokeOvenSlave() {
        super(CokeOvenMultiblock.instance, 0, false);
        this.shouldDropInventory = false;
    }

    @Override public void update() {
        if (isDummy()) { ICTickingRegistry.removeFromTicking(this); }
        super.update();
        if (world.isRemote) { return; }
        if (master() != null) {
            loadGrace = 0;
            return;
        }
        loadGrace++;
        if (loadGrace > 100) {
            formed = false;
            offset = new int[]{0, 0, 0};
            world.getChunk(getPos()).markDirty();
            markContainingBlockForUpdate(null);
        }
    }

    @Override public boolean interact(@Nonnull EnumFacing side, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull ItemStack heldItem, float hitX, float hitY, float hitZ) {
        TileEntityCokeOvenMaster m = master();
        if (formed && m != null && CokeOvenDoor.use(m, player, hand, heldItem, side, posInMultiblock())) { return true; }
        return super.interact(side, player, hand, heldItem, hitX, hitY, hitZ);
    }

    @Override public boolean isDummy() { return true; }

    @Override public int getGuiID() { return IFabGuiIds.COKE_OVEN; }

    @Override public boolean canOpenGui() { return formed; }

    @Override @Nullable public TileEntity getGuiMaster() { return master(); }

    @Override public TileEntityCokeOvenMaster master() { return resolveMaster(TileEntityCokeOvenMaster.class); }

    @Override protected GenericShape getShapeGetter() { return IFabShapes.get("coke_oven"); }

    @Override protected boolean useMirroredShape() { return false; }

    @Override public NonNullList<ItemStack> getInventory() { return NonNullList.create(); }

    @Override public boolean isStackValid(int slot, ItemStack stack) { return false; }

    @Override public int getSlotLimit(int slot) { return 64; }

    @Override @Nonnull public IFluidTank[] getInternalTanks() { return NO_TANKS; }

    @Override protected @Nonnull ICMultiblockRecipe readRecipeFromNBT(@Nonnull NBTTagCompound tag) { return CokeOvenRecipe.loadFromNBT(tag); }

    @Override @Nonnull public int[] getOutputTanks() { return NO_OUTPUT_TANKS; }

    @Override public boolean additionalCanProcessCheck(@Nonnull MultiblockProcess<ICMultiblockRecipe> process) { return true; }

    @Override public int getMaxProcessPerTick() { return 1; }

    @Override public int getProcessQueueMaxLength() { return 1; }

    @Override protected @Nonnull IFluidTank[] getAccessibleFluidTanks(@Nullable EnumFacing side, BlockPos position) {
        TileEntityCokeOvenMaster m = master();
        return m != null && m != this ? m.getAccessibleFluidTanks(side, position) : NO_TANKS;
    }

    @Override protected boolean canFillTankFrom(int iTank, @Nonnull EnumFacing side, @Nonnull FluidStack resource, BlockPos position) { return false; }

    @Override protected boolean canDrainTankFrom(int iTank, @Nonnull EnumFacing side, BlockPos position) {
        TileEntityCokeOvenMaster m = master();
        return m != null && m != this && m.canDrainTankFrom(iTank, side, position);
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        TileEntityCokeOvenMaster m = master();
        if (m != null && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return m.isPoI("item_input0", facing, posInMultiblock()); }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked") @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        TileEntityCokeOvenMaster m = master();
        if (m != null && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && m.isPoI("item_input0", facing, posInMultiblock())) { return (T)m.getInput(); }
        return super.getCapability(capability, facing);
    }
}
