package mctmods.immersivefabrication.client.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjLoader;

public final class FluidPipeGeometryLoader implements IGeometryLoader<FluidPipeGeometry> {
    @Override public FluidPipeGeometry read(JsonObject json, JsonDeserializationContext context) { return new FluidPipeGeometry(ObjLoader.INSTANCE.read(json, context)); }
}
