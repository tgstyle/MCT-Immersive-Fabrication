package mctmods.immersivefabrication.common.items;

import com.immersiveconvergence.api.block.BlockContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class HammerItem extends CraftingToolItem {
    public static final String TOOL_CLASS = "IFAB_HAMMER";
    public static final int DURABILITY = 100;

    public HammerItem(BlockContext context) { super(context, "hammer", DURABILITY); }

    @Override @Nonnull public Set<String> getToolClasses(@Nonnull ItemStack stack) { return Collections.singleton(TOOL_CLASS); }

    @Override public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) { return true; }
}
