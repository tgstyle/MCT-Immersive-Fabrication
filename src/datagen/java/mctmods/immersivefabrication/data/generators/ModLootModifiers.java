package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.loot.AddItemModifier;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.ToolActions;
import net.minecraft.resources.ResourceLocation;

public class ModLootModifiers extends GlobalLootModifierProvider {
    public ModLootModifiers(PackOutput output) { super(output, ImmersiveFabrication.MODID); }

    private static LootItemCondition.Builder table(String path) { return LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/" + path)); }

    @Override protected void start() {
        LootItemCondition.Builder grass = AnyOfCondition.anyOf(table("grass"), table("tall_grass"), table("fern"), table("large_fern"));
        LootItemCondition.Builder notShears = InvertedLootItemCondition.invert(CanToolPerformAction.canToolPerformAction(ToolActions.SHEARS_DIG));
        add("flax_seeds_from_grass", new AddItemModifier(new LootItemCondition[]{grass.build(), notShears.build(), LootItemRandomChanceCondition.randomChance(0.04F).build()}, IFabItems.SEEDS.get()));
    }
}
