package mctmods.immersivefabrication.data;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.data.generators.BlockLoot;
import mctmods.immersivefabrication.data.generators.ItemModel;
import mctmods.immersivefabrication.data.generators.ModBlockState;
import mctmods.immersivefabrication.data.generators.ModBlockTags;
import mctmods.immersivefabrication.data.generators.ModFluidTags;
import mctmods.immersivefabrication.data.generators.ModItemTags;
import mctmods.immersivefabrication.data.generators.ModLootModifiers;
import mctmods.immersivefabrication.data.generators.ModWorldgen;
import mctmods.immersivefabrication.data.generators.Recipes;
import mctmods.immersivefabrication.data.generators.WireLoot;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ImmersiveFabrication.MODID) public class DataGenerators {
    @SubscribeEvent public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput out = generator.getPackOutput();
        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockState(out, helper));
            generator.addProvider(true, new ItemModel(out, helper));
        }
        if (event.includeServer()) {
            ModBlockTags blockTags = new ModBlockTags(out, event.getLookupProvider(), helper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new ModItemTags(out, event.getLookupProvider(), blockTags.contentsGetter(), helper));
            generator.addProvider(true, new ModFluidTags(out, event.getLookupProvider(), helper));
            generator.addProvider(true, new Recipes(out, event.getLookupProvider()));
            generator.addProvider(true, new LootTableProvider(out, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
            generator.addProvider(true, new WireLoot(out, event.getLookupProvider()));
            generator.addProvider(true, new ModLootModifiers(out, event.getLookupProvider()));
            generator.addProvider(true, new ModWorldgen(out, event.getLookupProvider()));
        }
    }
}
