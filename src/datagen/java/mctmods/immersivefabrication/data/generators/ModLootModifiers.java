package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.loot.AddItemModifier;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModLootModifiers extends GlobalLootModifierProvider {
    public ModLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) { super(output, registries, ImmersiveFabrication.MODID); }

    private static LootItemCondition.Builder table(String path) { return LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/" + path)); }

    @Override protected void start() {
        LootItemCondition.Builder grass = AnyOfCondition.anyOf(table("short_grass"), table("tall_grass"), table("fern"), table("large_fern"));
        LootItemCondition.Builder notShears = InvertedLootItemCondition.invert(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_DIG));
        add("seed_flax_from_grass", new AddItemModifier(new LootItemCondition[]{grass.build(), notShears.build(), LootItemRandomChanceCondition.randomChance(0.04F).build()}, IFabItems.SEED_FLAX.get()));
    }
}
