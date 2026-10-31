package mctmods.immersivefabrication.client.models;

import mctmods.immersivefabrication.ImmersiveFabrication;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.obj.ObjModel;

import javax.annotation.Nonnull;
import java.util.function.Function;

public final class IndustrialPumpGeometry implements IUnbakedGeometry<IndustrialPumpGeometry> {
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "industrial_pump_base");
    static final String[] KINDS = {"side", "bottom"};
    static final String[] STATES = {"none", "in", "out"};
    private static final Object2BooleanMap<String> BASE = baseParts();
    private final ObjModel model;

    IndustrialPumpGeometry(ObjModel model) { this.model = model; }

    private static Object2BooleanMap<String> baseParts() {
        Object2BooleanMap<String> parts = new Object2BooleanOpenHashMap<>();
        parts.put("cube", true);
        parts.put("cube_motor", false);
        parts.put("cube_shaft", false);
        return parts;
    }

    @Override @SuppressWarnings("deprecation") @Nonnull public BakedModel bake(@Nonnull IGeometryBakingContext context, @Nonnull ModelBaker baker, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
        IGeometryBakingContext part = StandaloneGeometryBakingContext.builder(context).withVisibleComponents(BASE).build(NAME);
        BakedModel base = model.bake(part, baker, spriteGetter, modelState, overrides);
        return new IndustrialPumpBakedModel(base, base.getQuads(null, null, RandomSource.create()), sprites(spriteGetter));
    }

    private static TextureAtlasSprite[][] sprites(Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite[][] sprites = new TextureAtlasSprite[KINDS.length][STATES.length];
        for (int kind = 0; kind < KINDS.length; kind++) {
            for (int state = 0; state < STATES.length; state++) { sprites[kind][state] = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(ImmersiveFabrication.MODID, "block/industrial_pump_" + KINDS[kind] + "_" + STATES[state]))); }
        }
        return sprites;
    }
}
