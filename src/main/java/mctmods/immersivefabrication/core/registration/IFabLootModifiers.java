package mctmods.immersivefabrication.core.registration;

import com.mojang.serialization.Codec;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.loot.AddItemModifier;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class IFabLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ImmersiveFabrication.MODID);

    static { REGISTER.register("add_item", () -> AddItemModifier.CODEC); }

    private IFabLootModifiers() {}

    public static void register(IEventBus bus) { REGISTER.register(bus); }
}
