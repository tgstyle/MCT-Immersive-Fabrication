package mctmods.immersivefabrication.core.registration;

import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.core.config.ICCommonConfig;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public final class IFabWireTypes {
    public static final List<ICWireType> TYPES = new ArrayList<>();
    private static final double SLACK = 1.005;
    private static final double THIN = 0.03125;
    private static final double THICK = 0.0625;
    private static final double LV_LOSS = 0.0125 / 16;
    private static final double MV_LOSS = 0.003 / 16;
    private static final double HV_LOSS = 0.0008 / 32;

    private IFabWireTypes() {}

    public static void register() {
        add("copper", ICWireCategory.LV, () -> ICCommonConfig.wires.lvRate, 16, LV_LOSS, THIN, 0xc15a36, ICWireCategory.LV.getDefaultShock(), true);
        add("copper_insulated", ICWireCategory.LV, () -> ICCommonConfig.wires.lvRate, 16, LV_LOSS, THIN, 0xfaf1de, 0, false);
        add("aluminum", ICWireCategory.MV, () -> ICCommonConfig.wires.mvRate, 16, MV_LOSS, THIN, 0x9aa0ae, ICWireCategory.MV.getDefaultShock(), true);
        add("aluminum_insulated", ICWireCategory.MV, () -> ICCommonConfig.wires.mvRate, 16, MV_LOSS, THIN, 0x9d857a, 0, false);
        add("acsr", ICWireCategory.HV, () -> ICCommonConfig.wires.hvRate, 32, HV_LOSS, THICK, 0x8e93a2, ICWireCategory.HV.getDefaultShock(), true);
        add("rope", ICWireCategory.STRUCTURE, () -> 0, 32, 0, THICK, 0x967e6d, 0, true);
        add("steel_cable", ICWireCategory.STRUCTURE, () -> 0, 32, 0, THICK, 0x3a3a3c, 0, false);
        ImmersiveFabrication.LOGGER.info("Registered {} wire types through Immersive Convergence: {}", TYPES.size(), TYPES);
    }

    private static void add(String name, ICWireCategory category, IntSupplier rate, int maxLength, double loss, double diameter, int color, float shock, boolean categoryDefault) {
        int meta = TYPES.size();
        ICWireType type = new ICWireType(name, category, rate, maxLength, loss, () -> diameter, SLACK, color, () -> new ItemStack(IFabItems.spool, 1, meta), shock);
        TYPES.add(categoryDefault ? ICWireType.registerDefault(type) : ICWireType.register(type));
    }
}
