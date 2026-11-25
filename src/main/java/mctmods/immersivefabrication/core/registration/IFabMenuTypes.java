package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.gui.CokeOvenMenu;
import mctmods.immersivefabrication.common.multiblocks.stone.cokeoven.logic.CokeOvenLogic;

import com.immersiveconvergence.api.gui.ArgContainer;
import com.immersiveconvergence.api.gui.BaseContainerMenu;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.function.Supplier;

public final class IFabMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ImmersiveFabrication.MODID);
    public static final MultiblockContainer<CokeOvenLogic.State, CokeOvenMenu> COKE_OVEN = new MultiblockContainer<>(ArgContainer.registerType(REGISTER, "coke_oven", (type, id, inv, buffer) -> CokeOvenMenu.makeClient(type, id, inv)), CokeOvenMenu::makeServer);

    private IFabMenuTypes() {}

    public static void register(IEventBus bus) { REGISTER.register(bus); }

    public static class MultiblockContainer<S extends IMultiblockState, C extends BaseContainerMenu> extends ArgContainer<BaseContainerMenu.MultiblockMenuContext<S>, C> {
        private MultiblockContainer(Supplier<MenuType<C>> type, ArgContainer.IArgContainerConstructor<BaseContainerMenu.MultiblockMenuContext<S>, C> factory) { super(type, factory); }

        public MenuProvider provide(IMultiblockContext<S> ctx, BlockPos relativeClicked) { return this.provide(new BaseContainerMenu.MultiblockMenuContext<>(ctx, ctx.getLevel().toAbsolute(relativeClicked))); }
    }
}
