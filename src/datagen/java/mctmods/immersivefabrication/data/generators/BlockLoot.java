package mctmods.immersivefabrication.data.generators;

import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.util.IFabBlockNames;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabItems;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockLoot extends BlockLootSubProvider {
    private final List<Block> known = new ArrayList<>();

    public BlockLoot() { super(Set.of(), FeatureFlags.REGISTRY.allFlags()); }

    private static Item item(String id) { return IFabItems.ENTRIES.get(id).get(); }

    private static boolean isOre(String id, String metal) { return id.equals(metal + "_ore") || id.equals("deepslate_" + metal + "_ore"); }

    private static String oreMetal(String id) {
        for (String metal : IFabBlockNames.ORE_METALS) { if (isOre(id, metal)) { return metal; } }
        return null;
    }

    private void table(Block block, LootTable.Builder builder) {
        known.add(block);
        add(block, builder);
    }

    private void entry(String id, Block block) {
        String metal = oreMetal(id);
        if (metal != null) { table(block, createOreDrop(block, item("raw_" + metal))); }
        else if (id.endsWith("_slab")) { table(block, createSlabItemTable(block)); }
        else if (id.equals("industrial_pump")) { table(block, pump(block)); }
        else if (id.equals("fluid_pipe")) { table(block, pipe(block)); }
        else { known.add(block); dropSelf(block); }
    }

    private LootTable.Builder pipe(Block block) {
        return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("color", "color")))));
    }

    private LootTable.Builder pump(Block block) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(ExplosionCondition.survivesExplosion())
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
                .add(LootItem.lootTableItem(block)));
    }

    private LootTable.Builder flax(Block block) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlaxBlock.TYPE, "stage5")))
                .add(LootItem.lootTableItem(item("flax_fiber")).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.75F))))
                .add(LootItem.lootTableItem(IFabItems.SEEDS.get())));
    }

    @Override protected void generate() {
        IFabBlocks.ENTRIES.forEach((id, block) -> entry(id, block.get()));
        table(IFabBlocks.FLAX.get(), flax(IFabBlocks.FLAX.get()));
    }

    @Override @Nonnull protected Iterable<Block> getKnownBlocks() { return known; }
}
