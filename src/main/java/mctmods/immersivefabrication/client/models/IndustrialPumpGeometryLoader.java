package mctmods.immersivefabrication.client.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.obj.ObjLoader;

import javax.annotation.Nonnull;

public final class IndustrialPumpGeometryLoader implements IGeometryLoader<IndustrialPumpGeometry> {
    @Override @Nonnull public IndustrialPumpGeometry read(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) { return new IndustrialPumpGeometry(ObjLoader.INSTANCE.read(json, context)); }
}
