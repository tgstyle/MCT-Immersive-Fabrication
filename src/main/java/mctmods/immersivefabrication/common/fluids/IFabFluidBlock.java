package mctmods.immersivefabrication.common.fluids;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public class IFabFluidBlock extends BlockFluidClassic {
    private final int flammability;
    private final int fireSpread;

    public IFabFluidBlock(Fluid fluid, String name, int flammability, int fireSpread) {
        super(fluid, Material.WATER);
        this.flammability = flammability;
        this.fireSpread = fireSpread;
        setRegistryName(ImmersiveFabrication.MODID, name);
        setTranslationKey(ImmersiveFabrication.MODID + "." + name);
    }

    @Override public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return fireSpread; }

    @Override public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return flammability > 0; }
}
