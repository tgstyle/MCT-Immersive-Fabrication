package mctmods.immersivefabrication.data.generators;

import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.wires.fittings.ICPlatedFittingBlock;

import com.immersiveconvergence.api.fluid.FluidEntry;

import mctmods.immersivefabrication.ImmersiveFabrication;
import mctmods.immersivefabrication.common.blocks.metal.structure.HeatingElementBlock;
import mctmods.immersivefabrication.common.blocks.metal.structure.IndustrialPumpBlock;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxBlock;
import mctmods.immersivefabrication.common.blocks.plant.flax.FlaxStage;
import mctmods.immersivefabrication.common.util.IFabBlockNames;
import mctmods.immersivefabrication.core.registration.IFabBlocks;
import mctmods.immersivefabrication.core.registration.IFabFluids;
import mctmods.immersivefabrication.core.registration.IFabMultiblocks;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockState extends BlockStateProvider {
    public ModBlockState(PackOutput output, ExistingFileHelper helper) { super(output, ImmersiveFabrication.MODID, helper); }

    private static Block block(String id) { return Objects.requireNonNull(IFabBlocks.ENTRIES.containsKey(id) ? IFabBlocks.ENTRIES.get(id) : IFabBlocks.WIRES.get(id), id).get(); }

    private ResourceLocation tex(String path) { return modLoc("block/" + path); }

    private ModelFile existing(String name) { return models().getExistingFile(modLoc("block/" + name)); }

    private static int[] fitting(Direction direction) { return switch (direction) { case DOWN -> new int[]{0, 0}; case UP -> new int[]{180, 0}; case NORTH -> new int[]{90, 180}; case SOUTH -> new int[]{90, 0}; case WEST -> new int[]{90, 90}; case EAST -> new int[]{90, 270}; }; }

    private static int[] heater(Direction direction) { return switch (direction) { case NORTH -> new int[]{0, 0}; case EAST -> new int[]{0, 90}; case SOUTH -> new int[]{0, 180}; case WEST -> new int[]{0, 270}; case UP -> new int[]{270, 0}; case DOWN -> new int[]{90, 0}; }; }

    private static int[] transformer(Direction direction) { return switch (direction) { case WEST -> new int[]{0, 90}; case NORTH -> new int[]{0, 180}; case EAST -> new int[]{0, 270}; default -> new int[]{0, 0}; }; }

    private static void place(PartialBlockstate state, ModelFile model, int[] rotation) { state.modelForState().modelFile(model).rotationX(rotation[0]).rotationY(rotation[1]).addModel(); }

    private static BlockModelBuilder cutout(BlockModelBuilder model, boolean cutout) {
        if (cutout) { model.renderType("minecraft:cutout"); }
        return model;
    }

    private void cube(String id, String texture) { simpleBlock(block(id), models().cubeAll(id, tex(texture))); }

    private void column(String id, String side, String end) { simpleBlock(block(id), models().cubeColumn(id, tex(side), tex(end))); }

    private void slabs(String id, ModelFile base, ResourceLocation side, ResourceLocation end, boolean cutout) {
        ModelFile bottom = cutout(models().slab(id + "_slab", side, side, end), cutout);
        ModelFile top = cutout(models().slabTop(id + "_slab_top", side, side, end), cutout);
        slabBlock((SlabBlock) block(id + "_slab"), bottom, top, base);
    }

    private void stairs(String id, ResourceLocation side, ResourceLocation end, boolean cutout) {
        ModelFile straight = cutout(models().stairs(id + "_stairs", side, side, end), cutout);
        ModelFile inner = cutout(models().stairsInner(id + "_stairs_inner", side, side, end), cutout);
        ModelFile outer = cutout(models().stairsOuter(id + "_stairs_outer", side, side, end), cutout);
        stairsBlock((StairBlock) block(id + "_stairs"), straight, inner, outer);
    }

    private void family(String id, String texture, boolean withStairs) {
        ModelFile base = models().cubeAll(id, tex(texture));
        simpleBlock(block(id), base);
        slabs(id, base, tex(texture), tex(texture), false);
        if (withStairs) { stairs(id, tex(texture), tex(texture), false); }
    }

    private void scaffolding(String metal, String type) {
        String id = metal + "_scaffolding_" + type;
        ResourceLocation side = tex("metal/scaffolding_" + metal);
        ResourceLocation end = type.equals("standard") ? side : tex("metal/scaffolding_" + metal + "_" + type);
        ModelFile base = cutout(type.equals("standard") ? models().cubeAll(id, side) : models().cubeBottomTop(id, side, side, end), true);
        simpleBlock(block(id), base);
        slabs(id, base, side, end, true);
        stairs(id, side, end, true);
    }

    private void fence(String id, String texture) {
        ResourceLocation texturePath = tex(texture);
        ModelFile post = models().fencePost(id + "_post", texturePath);
        ModelFile side = models().withExistingParent(id + "_side", modLoc("block/fence_side")).texture("texture", texturePath);
        models().fenceInventory(id + "_inventory", texturePath);
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block(id));
        builder.part().modelFile(post).addModel().end();
        BooleanProperty[] sides = {BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST};
        for (int i = 0; i < sides.length; i++) { builder.part().modelFile(side).rotationY(i * 90).uvLock(true).addModel().condition(sides[i], true); }
    }

    private void fittings(String id) {
        ModelFile plated = existing(id);
        ModelFile bare = existing(id + "_noplate");
        for (Direction direction : Direction.values()) {
            place(getVariantBuilder(block(id)).partialState().with(BlockStateProperties.FACING, direction).with(ICPlatedFittingBlock.PLATE, true), plated, fitting(direction));
            place(getVariantBuilder(block(id)).partialState().with(BlockStateProperties.FACING, direction).with(ICPlatedFittingBlock.PLATE, false), bare, fitting(direction));
        }
    }

    private void anchor() {
        for (Direction direction : Direction.values()) { place(getVariantBuilder(block("anchor")).partialState().with(BlockStateProperties.FACING, direction), existing("anchor"), fitting(direction)); }
    }

    private void transformers() {
        for (String id : new String[]{"transformer", "transformer_hv"}) {
            for (Direction direction : Direction.values()) { place(getVariantBuilder(block(id)).partialState().with(BlockStateProperties.FACING, direction), existing(id), transformer(direction)); }
        }
    }

    private void heatingElement() {
        for (boolean active : new boolean[]{false, true}) {
            ModelFile model = existing(active ? "heating_element_active" : "heating_element");
            for (Direction direction : Direction.values()) { place(getVariantBuilder(block("heating_element")).partialState().with(HeatingElementBlock.ACTIVE, active).with(HeatingElementBlock.FACING, direction), model, heater(direction)); }
        }
    }

    private void industrialPump() {
        getVariantBuilder(block("industrial_pump")).partialState().with(IndustrialPumpBlock.HALF, DoubleBlockHalf.LOWER).modelForState().modelFile(existing("industrial_pump_lower")).addModel().partialState().with(IndustrialPumpBlock.HALF, DoubleBlockHalf.UPPER).modelForState().modelFile(existing("industrial_pump")).addModel();
    }

    private void flax() {
        for (FlaxStage stage : FlaxStage.values()) {
            String name = "flax_" + stage.getSerializedName();
            ModelFile model = cutout(models().crop(name, tex("flax/" + stage.getSerializedName())), true);
            getVariantBuilder(IFabBlocks.FLAX.get()).partialState().with(FlaxBlock.TYPE, stage).modelForState().modelFile(model).addModel();
        }
    }

    private void treatedLog() {
        ResourceLocation side = tex("wooden/treated_log");
        ResourceLocation end = tex("wooden/treated_log_top");
        ModelFile upright = models().cubeColumn("treated_log", side, end);
        ModelFile lying = models().cubeColumnHorizontal("treated_log_horizontal", side, end);
        getVariantBuilder(block("treated_log")).partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(upright).addModel().partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().modelFile(lying).rotationX(90).rotationY(90).addModel().partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().modelFile(lying).rotationX(90).addModel();
    }

    private void metals() {
        for (String metal : IFabBlockNames.STORAGE_METALS) {
            if (metal.equals("uranium")) { column("uranium_block", "metal/uranium_side", "metal/uranium_top"); } else { cube(metal + "_block", "metal/" + metal); }
        }
        for (String metal : IFabBlockNames.ORE_METALS) {
            cube(metal + "_ore", "metal/ore_" + metal);
            cube("deepslate_" + metal + "_ore", "metal/deepslate_ore_" + metal);
            cube("raw_" + metal + "_block", "metal/raw_block_" + metal);
        }
        for (String metal : IFabBlockNames.CLADDING_METALS) { family(metal + "_cladding", "metal/cladding_" + metal, false); }
    }

    private void machines() {
        for (String name : IFabBlockNames.ENGINEERING) {
            if (name.equals("coil_acsr")) { column(name, "metal/coil_acsr_side", "metal/coil_acsr_top"); } else { cube(name, "metal/" + name); }
        }
        cube("fluid_placer", "metal/fluid_placer_side_none");
        column("metal_barrel", "metal/barrel_iron_side", "metal/barrel_iron_none");
        simpleBlock(block("fluid_pipe"), existing("fluid_pipe"));
        heatingElement();
        industrialPump();
    }

    private void stone() {
        for (String name : IFabBlockNames.CONCRETES) { family(name, "stone/" + name, true); }
        family("coke_brick", "stone/cokebrick", false);
        family("blast_brick", "stone/blastbrick", false);
        family("blast_brick_reinforced", "stone/blastbrick_reinforced", false);
        family("alloy_brick", "stone/alloybrick", false);
        cube("coal_coke_block", "coke");
    }

    private void frames() {
        for (String metal : IFabBlockNames.FRAME_METALS) {
            fence(metal + "_fence", "metal/" + metal);
            for (String type : IFabBlockNames.SCAFFOLDING_TYPES) { scaffolding(metal, type); }
        }
    }

    private void wood() {
        for (String style : IFabBlockNames.TREATED_WOOD) { family("treated_wood_" + style, "wooden/treated_wood_" + style, true); }
        fence("treated_wood_fence", "wooden/treated_wood_horizontal");
        treatedLog();
    }

    private void wires() {
        for (String id : new String[]{"terminal_lv", "insulator_lv", "terminal_mv", "insulator_mv", "terminal_hv", "insulator_hv"}) { fittings(id); }
        anchor();
        transformers();
    }

    private void cokeOven() {
        ModelFile body = existing("coke_oven");
        ModelFile empty = models().getBuilder("coke_oven_empty").texture("particle", modLoc("multiblock/stone/coke_oven"));
        getVariantBuilder(IFabMultiblocks.COKE_OVEN.block().get()).forAllStates(state -> {
            if (state.getValue(ModProperties.MULTIBLOCKSLAVE)) { return ConfiguredModel.builder().modelFile(empty).build(); }
            return ConfiguredModel.builder().modelFile(body).rotationY((int) ((state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)).build();
        });
    }

    private void fluids() {
        for (FluidEntry entry : IFabFluids.ENTRIES) { simpleBlock(entry.getBlock(), existing(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(entry.getBlock())).getPath())); }
    }

    @Override protected void registerStatesAndModels() {
        metals();
        machines();
        stone();
        frames();
        wood();
        wires();
        flax();
        cokeOven();
        fluids();
    }
}
