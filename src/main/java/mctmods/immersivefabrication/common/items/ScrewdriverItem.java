package mctmods.immersivefabrication.common.items;

import mctmods.immersivefabrication.common.blocks.metal.structure.logic.HeatingElementBlockEntity;
import mctmods.immersivefabrication.common.blocks.metal.structure.logic.IndustrialPumpBlockEntity;

import com.immersiveconvergence.api.wires.fittings.ICFittingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem() { super(new Item.Properties().stacksTo(1)); }

    @Override public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) { return true; }

    @Override @Nonnull public InteractionResult onItemUseFirst(@Nonnull ItemStack stack, @Nonnull UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof IndustrialPumpBlockEntity pump) { return configurePump(pump, context); }
        if (blockEntity instanceof HeatingElementBlockEntity heater) { return turnHeater(heater, context); }
        if (!(blockEntity instanceof ICFittingBlockEntity fitting) || !fitting.platable()) { return InteractionResult.PASS; }
        if (!level.isClientSide) { fitting.togglePlate(); }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static InteractionResult configurePump(IndustrialPumpBlockEntity pump, UseOnContext context) {
        Player player = context.getPlayer();
        Direction face = player != null && player.isShiftKeyDown() ? context.getClickedFace().getOpposite() : context.getClickedFace();
        if (!pump.configurable(face)) { return InteractionResult.PASS; }
        Level level = context.getLevel();
        if (!level.isClientSide) { pump.cycle(face); }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static InteractionResult turnHeater(HeatingElementBlockEntity heater, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (!level.isClientSide) { heater.turn(context.getClickedFace(), player != null && player.isShiftKeyDown()); }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) { return true; }

    @Override @Nonnull public ItemStack getCraftingRemainingItem(@Nonnull ItemStack stack) { return stack.copyWithCount(1); }
}
