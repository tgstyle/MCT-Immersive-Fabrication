package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import com.immersiveconvergence.api.fluid.ICPipes;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.api.util.ICFluidUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FluidPipeNetwork {
    private final Level level;
    private final BlockEntity[] tiles;
    private final IFluidHandler[] handlers;
    private final long[] positions;
    private final boolean[] foreign;
    private final int[] accept;
    private final int[] given;
    private final int size;
    private int last;
    private boolean valid = true;
    private boolean busy;
    private FluidStack keep = FluidStack.EMPTY;
    private FluidStack strip = FluidStack.EMPTY;

    private FluidPipeNetwork(Level level, List<BlockEntity> tiles, List<IFluidHandler> handlers) {
        this.level = level;
        this.size = tiles.size();
        this.tiles = tiles.toArray(new BlockEntity[0]);
        this.handlers = handlers.toArray(new IFluidHandler[0]);
        this.positions = new long[size];
        this.foreign = new boolean[size];
        this.accept = new int[size];
        this.given = new int[size];
        for (int i = 0; i < size; i++) {
            positions[i] = this.tiles[i].getBlockPos().asLong();
            foreign[i] = IICFluidPipe.is(this.tiles[i]);
        }
    }

    static FluidPipeNetwork build(FluidPipeBlockEntity start) {
        Level level = start.getLevelNonnull();
        List<FluidPipeBlockEntity> members = new ArrayList<>();
        List<BlockEntity> tiles = new ArrayList<>();
        List<IFluidHandler> handlers = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        Set<FluidPipeBlockEntity> visited = new HashSet<>();
        ArrayDeque<FluidPipeBlockEntity> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            FluidPipeBlockEntity pipe = queue.removeFirst();
            members.add(pipe);
            for (Direction f : DirectionUtils.VALUES) {
                if (!pipe.isConnected(f)) { continue; }
                BlockPos at = pipe.getBlockPos().relative(f);
                BlockEntity be = SafeChunkUtils.getExistingBlockEntity(level, at);
                if (be == null || be.isRemoved()) { continue; }
                if (be instanceof FluidPipeBlockEntity next) {
                    if (next.isConnected(f.getOpposite()) && visited.add(next)) { queue.add(next); }
                    continue;
                }
                if (seen.contains(at)) { continue; }
                IFluidHandler handler = be.getCapability(ForgeCapabilities.FLUID_HANDLER, f.getOpposite()).resolve().orElse(null);
                if (handler == null) { continue; }
                seen.add(at);
                tiles.add(be);
                handlers.add(handler);
            }
        }
        FluidPipeNetwork net = new FluidPipeNetwork(level, tiles, handlers);
        for (FluidPipeBlockEntity member : members) { member.network = net; }
        return net;
    }

    boolean isValid() { return valid; }

    void invalidate() { valid = false; }

    int fill(FluidStack resource, long origin, boolean execute) {
        if (busy || !valid || resource.isEmpty() || size == 0) { return 0; }
        busy = true;
        try { return transfer(resource, origin, execute); }
        finally { busy = false; }
    }

    private int transfer(FluidStack resource, long origin, boolean execute) {
        boolean pressurized = IICFluidPipe.isPressurized(resource) || ICPipes.isNormallyPressurized(resource.getFluid());
        int limit = Math.min(resource.getAmount(), ICPipes.getTransferableAmount(pressurized));
        if (!keep.isFluidEqual(resource)) {
            keep = resource.copy();
            strip = ICFluidUtils.copyFluidStackWithAmount(resource, limit, true);
        }
        int total = 0;
        int open = 0;
        for (int i = 0; i < size; i++) {
            accept[i] = 0;
            given[i] = 0;
            BlockEntity tile = tiles[i];
            if (positions[i] == origin || !level.isLoaded(tile.getBlockPos())) { continue; }
            if (tile.isRemoved() || SafeChunkUtils.getExistingBlockEntity(level, tile.getBlockPos()) != tile) {
                valid = false;
                continue;
            }
            FluidStack probe = foreign[i] ? keep : strip;
            probe.setAmount(limit);
            int taken = Math.min(handlers[i].fill(probe, FluidAction.SIMULATE), limit);
            if (taken <= 0) { continue; }
            accept[i] = taken;
            total += taken;
            open++;
        }
        if (total == 0) { return 0; }
        int amount = Math.min(limit, total);
        if (ICPipes.lastServed()) { serveLast(amount); }
        else { spread(amount, open); }
        if (!execute) { return amount; }
        int filled = 0;
        for (int i = 0; i < size; i++) { if (given[i] > 0) { filled += handlers[i].fill(ICFluidUtils.copyFluidStackWithAmount(resource, given[i], !foreign[i]), FluidAction.EXECUTE); } }
        return filled;
    }

    private void spread(int amount, int open) {
        int left = amount;
        while (left > 0 && open > 0) {
            int share = Math.max(1, left / open);
            for (int i = 0; i < size && left > 0; i++) {
                int room = accept[i] - given[i];
                if (room <= 0) { continue; }
                int part = Math.min(Math.min(room, share), left);
                given[i] += part;
                left -= part;
                if (given[i] == accept[i]) { open--; }
            }
        }
    }

    private void serveLast(int amount) {
        int left = amount;
        for (int k = 0; k < size && left > 0; k++) {
            int i = (last + k) % size;
            int part = Math.min(accept[i], left);
            if (part <= 0) { continue; }
            if (left == amount) { last = i; }
            given[i] = part;
            left -= part;
        }
    }
}
