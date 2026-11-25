package mctmods.immersivefabrication.common.multiblocks;

import mctmods.immersivefabrication.ImmersiveFabrication;

import com.immersiveconvergence.api.multiblock.ShapeData;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class IFabShapes {
    private static final Map<String, ShapeData> SHAPES = new ConcurrentHashMap<>();

    private IFabShapes() {}

    public static ShapeData get(String name) { return SHAPES.computeIfAbsent(name, id -> ShapeData.load(ImmersiveFabrication.MODID, id)); }
}
