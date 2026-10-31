package mctmods.immersivefabrication.data.generators;

import com.immersiveconvergence.api.fluid.FluidEntry;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabFluids;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ModFluidTags extends FluidTagsProvider {
    public ModFluidTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) { super(output, lookupProvider, ImmersiveFabrication.MODID, existingFileHelper); }

    @Override protected void addTags(@Nonnull Provider provider) {
        for (FluidEntry entry : IFabFluids.ENTRIES) {
            TagKey<Fluid> tag = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", BuiltInRegistries.FLUID.getKey(entry.getStill()).getPath()));
            tag(tag).add(entry.getStill());
        }
    }
}
