package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IFabDataComponents {
    private static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ImmersiveFabrication.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COLOR = REGISTER.registerComponentType("color", builder -> builder.persistent(Codec.intRange(0, 15)).networkSynchronized(ByteBufCodecs.VAR_INT));

    private IFabDataComponents() {}

    public static void register(IEventBus bus) { REGISTER.register(bus); }
}
