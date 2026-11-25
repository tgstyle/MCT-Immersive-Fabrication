package mctmods.immersivefabrication.common.multiblocks.stone;

import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenMaster;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.TileEntityCokeOvenSlave;
import mctmods.immersivefabrication.core.util.IFabContext;

import com.immersiveconvergence.api.block.ICBlockMultiblock;
import com.immersiveconvergence.api.block.ICItemBlockBase;
import com.immersiveconvergence.api.block.ICProperties;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nonnull;

public class StoneMultiblockBlock extends ICBlockMultiblock<StoneMultiblockType> {
    public StoneMultiblockBlock() {
        super(IFabContext.CONTEXT, "stone_multiblock", Material.ROCK, PropertyEnum.create("type", StoneMultiblockType.class), ICItemBlockBase.class, ICProperties.DYNAMICRENDER, ICProperties.BOOLEANS[0], Properties.AnimationProperty, ICProperties.OBJ_TEXTURE_REMAP);
        setHardness(2.0F);
        setResistance(20.0F);
        setAllNotNormalBlock();
        lightOpacity = 0;
    }

    @Override public boolean useCustomStateMapper() { return true; }

    @Override @Nonnull public String getCustomStateMapping(int meta, boolean itemBlock) { return enumValues[meta].getName(); }

    @Override @SuppressWarnings("deprecation") public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) { return true; }

    @Override public TileEntity createBasicTE(World world, StoneMultiblockType type) {
        switch (type) {
            case COKE_OVEN: return new TileEntityCokeOvenMaster();
            case COKE_OVEN_SLAVE: return new TileEntityCokeOvenSlave();
        }
        return null;
    }
}
