package mctmods.immersivefabrication.common.blocks.wooden;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.util.IFabContext;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;

public class TreatedLogBlock extends BlockRotatedPillar {
    public TreatedLogBlock() {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
        setRegistryName(ImmersiveFabrication.MODID, "treated_log");
        setTranslationKey(ImmersiveFabrication.MODID + ".treated_log");
        setCreativeTab(IFabContext.CONTEXT.creativeTab);
        setSoundType(SoundType.WOOD);
    }
}
