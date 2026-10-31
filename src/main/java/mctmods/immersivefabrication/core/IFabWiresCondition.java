package mctmods.immersivefabrication.core;

import mctmods.immersivefabrication.ImmersiveFabrication;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import javax.annotation.Nonnull;

public final class IFabWiresCondition implements ICondition {
    public static final IFabWiresCondition INSTANCE = new IFabWiresCondition();
    public static final MapCodec<IFabWiresCondition> CODEC = MapCodec.unit(INSTANCE);
    private static final DeferredRegister<MapCodec<? extends ICondition>> REGISTER = DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, ImmersiveFabrication.MODID);

    static { REGISTER.register("wires_enabled", () -> CODEC); }

    private IFabWiresCondition() {}

    public static void init(IEventBus bus) { REGISTER.register(bus); }

    @Override public boolean test(@Nonnull IContext context) { return IFabCommonConfig.wires(); }

    @Override public @Nonnull MapCodec<? extends ICondition> codec() { return CODEC; }

    @Override public String toString() { return "wires_enabled"; }
}
