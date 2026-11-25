package mctmods.immersivefabrication.common.multiblocks.stone.cokeoven;

import net.minecraft.nbt.NBTTagCompound;

public class CokeOvenCooking {
    public static final class Result {
        public final boolean[] consume = new boolean[2];
        public int gas;
        public int flue;
        public boolean finished;
    }

    private int progress;
    private int flueRemainder;
    private final int[] burn = new int[2];
    private final int[] burnMax = new int[2];
    private boolean paused;
    private boolean stepped;

    public Result tick(boolean wanted, int[] fuelBurnTimes, int rcogAmount, int flueAmount, boolean flueExists) {
        Result result = new Result();
        stepped = false;
        int n = 0;
        for (int i = 0; i < 2; i++) {
            if (wanted && (burn[i] > 0 || fuelBurnTimes[i] > 0)) {
                n++;
            }
        }
        if (n == 0) {
            paused = false;
            return result;
        }
        int newProgress = progress + n;
        int needGas = (int) ((long) newProgress * CokeOvenRules.GAS_PER_BLOCK / CokeOvenRules.COOK_UNITS - (long) progress * CokeOvenRules.GAS_PER_BLOCK / CokeOvenRules.COOK_UNITS);
        int needFlue = flueExists ? (flueRemainder + n) / CokeOvenRules.FLUE_BURN_TICKS_PER_MB : 0;
        if (rcogAmount >= CokeOvenRules.TANK || flueExists && flueAmount >= CokeOvenRules.TANK || rcogAmount + needGas > CokeOvenRules.TANK || flueExists && flueAmount + needFlue > CokeOvenRules.TANK) {
            paused = true;
            return result;
        }
        paused = false;
        stepped = true;
        for (int i = 0; i < 2; i++) {
            if (burn[i] == 0 && fuelBurnTimes[i] > 0) {
                result.consume[i] = true;
                burn[i] = fuelBurnTimes[i];
                burnMax[i] = fuelBurnTimes[i];
            }
            if (burn[i] > 0) {
                burn[i]--;
            }
        }
        progress = Math.min(newProgress, CokeOvenRules.COOK_UNITS);
        flueRemainder = (flueRemainder + n) % CokeOvenRules.FLUE_BURN_TICKS_PER_MB;
        result.gas = needGas;
        result.flue = needFlue;
        if (progress == CokeOvenRules.COOK_UNITS) {
            result.finished = true;
            progress = 0;
            flueRemainder = 0;
        }
        return result;
    }

    public void lose() {
        progress = 0;
        flueRemainder = 0;
        stepped = false;
    }

    public boolean cooking() {
        return stepped || paused;
    }

    public boolean lit() {
        return burn[0] > 0 || burn[1] > 0;
    }

    public boolean paused() {
        return paused;
    }

    public int progress() {
        return progress;
    }

    public int burn(int side) {
        return burn[side];
    }

    public void writeNBT(NBTTagCompound tag) {
        tag.setInteger("progress", progress);
        tag.setInteger("flueRemainder", flueRemainder);
        tag.setIntArray("burn", burn.clone());
        tag.setIntArray("burnMax", burnMax.clone());
        tag.setBoolean("paused", paused);
    }

    public void readNBT(NBTTagCompound tag) {
        progress = tag.getInteger("progress");
        flueRemainder = tag.getInteger("flueRemainder");
        readArray(tag.getIntArray("burn"), burn);
        readArray(tag.getIntArray("burnMax"), burnMax);
        paused = tag.getBoolean("paused");
        stepped = false;
    }

    private static void readArray(int[] source, int[] target) {
        for (int i = 0; i < target.length; i++) {
            target[i] = source.length == target.length ? source[i] : 0;
        }
    }
}
