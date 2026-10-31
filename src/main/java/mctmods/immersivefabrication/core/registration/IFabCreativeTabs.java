package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IFabCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ImmersiveFabrication.MODID);

    static { REGISTER.register("main", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(IFabItems.HAMMER.get()))
            .title(Component.translatable("itemGroup." + ImmersiveFabrication.MODID))
            .displayItems((params, output) -> IFabItems.REGISTER.getEntries().forEach(entry -> output.accept(entry.get())))
            .build()); }

    private IFabCreativeTabs() {}

    public static void register(IEventBus bus) { REGISTER.register(bus); }
}
