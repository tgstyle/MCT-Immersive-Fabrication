package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.common.blocks.metal.ore.OreType;
import mctmods.immersivefabrication.common.blocks.metal.structure.IndustrialPumpBlock;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxStage;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabDataComponents;
import mctmods.immersivefabrication.core.registration.IFabItems;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public class BlockLoot extends BlockLootSubProvider {
    private final List<Block> known = new ArrayList<>();

    public BlockLoot(HolderLookup.Provider registries) { super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries); }

    private static Item flaxFiber() { return IFabItems.REGISTER.getEntries().stream().filter(entry -> entry.getId().getPath().equals("flax_fiber")).findFirst().orElseThrow().get(); }

    private static OreType oreType(String id) {
        for (OreType type : OreType.values()) { if (id.equals(type.id()) || id.equals(type.deepslateId())) { return type; } }
        return null;
    }

    private void table(Block block, LootTable.Builder builder) {
        known.add(block);
        add(block, builder);
    }

    private void entry(String id, Block block) {
        OreType ore = oreType(id);
        if (ore != null) { table(block, createOreDrop(block, IFabItems.RAW_ITEMS.get(ore).get())); }
        else if (id.endsWith("_slab")) { table(block, slab(block)); }
        else if (id.equals("industrial_pump")) { table(block, pump(block)); }
        else if (id.equals("fluid_pipe")) { table(block, pipe(block)); }
        else { known.add(block); dropSelf(block); }
    }

    private LootTable.Builder pipe(Block block) {
        return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(IFabDataComponents.COLOR.get())))));
    }

    private LootTable.Builder pump(Block block) {
        return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(IndustrialPumpBlock.HALF, DoubleBlockHalf.LOWER)))
                .add(LootItem.lootTableItem(block)));
    }

    private LootTable.Builder slab(Block block) {
        return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)))
                .add(LootItem.lootTableItem(block))
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)))));
    }

    private LootTable.Builder flax(Block block) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlaxBlock.TYPE, FlaxStage.STAGE5)))
                .add(EntryGroup.list(
                        LootItem.lootTableItem(flaxFiber())
                                .apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.75F)))
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), 0.75F, 0)),
                        LootItem.lootTableItem(IFabItems.SEED_FLAX.get()))));
    }

    @Override protected void generate() {
        IFabBlocks.ALL.forEach((id, block) -> entry(id, block.get()));
        table(IFabBlocks.FLAX.get(), flax(IFabBlocks.FLAX.get()));
        table(IFabMultiblocks.COKE_OVEN.block().get(), LootTable.lootTable());
    }

    @Override @Nonnull protected Iterable<Block> getKnownBlocks() { return known; }
}
