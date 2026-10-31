package mctmods.immersivefabrication.data;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.core.registration.IFabWiresPack;
import mctmods.immersivefabrication.data.generators.BlockLoot;
import mctmods.immersivefabrication.data.generators.ItemModel;
import mctmods.immersivefabrication.data.generators.ModLootModifiers;
import mctmods.immersivefabrication.data.generators.ModBlockState;
import mctmods.immersivefabrication.data.generators.ModBlockTags;
import mctmods.immersivefabrication.data.generators.ModFluidTags;
import mctmods.immersivefabrication.data.generators.ModItemTags;
import mctmods.immersivefabrication.data.generators.ModWorldgen;
import mctmods.immersivefabrication.data.generators.Recipes;
import mctmods.immersivefabrication.data.generators.WireLoot;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ImmersiveFabrication.MODID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenerators {
    @SubscribeEvent public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput out = generator.getPackOutput();
        if (event.includeClient()) { generator.addProvider(true, new ModBlockState(out, helper));
            generator.addProvider(true, new ItemModel(out, helper));
        }
        if (event.includeServer()) {
            ModBlockTags blockTags = new ModBlockTags(out, event.getLookupProvider(), helper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new ModItemTags(out, event.getLookupProvider(), blockTags.contentsGetter(), helper));
            generator.addProvider(true, new ModFluidTags(out, event.getLookupProvider(), helper));
            generator.addProvider(true, new ModWorldgen(out, event.getLookupProvider()));
            generator.addProvider(true, new Recipes(out));
            generator.addProvider(true, new ModLootModifiers(out));
            generator.addProvider(true, new LootTableProvider(out, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK))));
            PackOutput wiresOut = generator.getPackOutput(IFabWiresPack.FOLDER);
            generator.addProvider(true, new PackMetadataGenerator(wiresOut).add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("${mod_id} resources"), 15)));
            LootTableProvider wireLoot = new LootTableProvider(wiresOut, Set.of(), List.of(new LootTableProvider.SubProviderEntry(WireLoot::new, LootContextParamSets.BLOCK)));
            generator.addProvider(true, new DataProvider() {
                @Override @Nonnull public CompletableFuture<?> run(@Nonnull CachedOutput cache) { return wireLoot.run(cache); }

                @Override @Nonnull public String getName() { return "Wire Loot Tables"; }
            });
        }
    }
}
