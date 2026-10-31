package mctmods.immersivefabrication.common.blocks.metal.structure;

import mctmods.immersivefabrication.common.util.IFabBlockEnum;

@SuppressWarnings("unused")
public enum StructureType implements IFabBlockEnum {
    FLUID_PIPE,
    FLUID_PLACER,
    HEATING_ELEMENT,
    METAL_BARREL,
    INDUSTRIAL_PUMP,
    INDUSTRIAL_PUMP_TOP {
        @Override public boolean listForCreative() { return false; }
    }
}
