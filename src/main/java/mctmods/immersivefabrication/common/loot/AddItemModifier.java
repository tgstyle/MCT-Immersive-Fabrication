package mctmods.immersivefabrication.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class AddItemModifier extends LootModifier {
    public static final Codec<AddItemModifier> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance).and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(modifier -> modifier.item)).apply(instance, AddItemModifier::new));
    private final Item item;

    public AddItemModifier(LootItemCondition[] conditions, Item item) {
        super(conditions);
        this.item = item;
    }

    @Override @Nonnull protected ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        generatedLoot.add(new ItemStack(item));
        return generatedLoot;
    }

    @Override public Codec<? extends IGlobalLootModifier> codec() { return CODEC; }
}
