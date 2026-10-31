package mctmods.immersivefabrication.core;

import mctmods.immersivefabrication.ImmersiveFabrication;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import javax.annotation.Nonnull;

public final class IFabWiresCondition implements ICondition {
    public static final IFabWiresCondition INSTANCE = new IFabWiresCondition();
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "wires_enabled");

    private IFabWiresCondition() {}

    @Override public ResourceLocation getID() { return NAME; }

    @Override public boolean test(@Nonnull IContext context) { return IFabCommonConfig.wires(); }

    @Override public String toString() { return "wires_enabled"; }

    public static final class Serializer implements IConditionSerializer<IFabWiresCondition> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override public void write(@Nonnull JsonObject json, @Nonnull IFabWiresCondition value) {}

        @Override public IFabWiresCondition read(@Nonnull JsonObject json) { return IFabWiresCondition.INSTANCE; }

        @Override public ResourceLocation getID() { return NAME; }
    }
}
