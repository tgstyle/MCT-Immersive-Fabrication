package mctmods.immersivefabrication.core;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unused") public final class IFabWiresConditionFactory implements IConditionFactory {
    @Override public BooleanSupplier parse(JsonContext context, JsonObject json) { return IFabCommonConfig::wires; }
}
