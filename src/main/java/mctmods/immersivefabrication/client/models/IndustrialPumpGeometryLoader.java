package mctmods.immersivefabrication.client.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjLoader;

public final class IndustrialPumpGeometryLoader implements IGeometryLoader<IndustrialPumpGeometry> {
    @Override public IndustrialPumpGeometry read(JsonObject json, JsonDeserializationContext context) { return new IndustrialPumpGeometry(ObjLoader.INSTANCE.read(json, context)); }
}
