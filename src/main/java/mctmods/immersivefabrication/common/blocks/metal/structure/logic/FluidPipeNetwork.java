package mctmods.immersivefabrication.common.blocks.metal.structure.logic;

import com.immersiveconvergence.api.fluid.ICPipes;
import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FluidPipeNetwork {
    private final World world;
    private final TileEntity[] tiles;
    private final IFluidHandler[] handlers;
    private final long[] positions;
    private final boolean[] foreign;
    private final int[] accept;
    private final int[] given;
    private final int size;
    private int last;
    private boolean valid = true;
    private boolean busy;
    private FluidStack keep;
    private FluidStack strip;

    private FluidPipeNetwork(World world, List<TileEntity> tiles, List<IFluidHandler> handlers) {
        this.world = world;
        this.size = tiles.size();
        this.tiles = tiles.toArray(new TileEntity[0]);
        this.handlers = handlers.toArray(new IFluidHandler[0]);
        this.positions = new long[size];
        this.foreign = new boolean[size];
        this.accept = new int[size];
        this.given = new int[size];
        for (int i = 0; i < size; i++) {
            positions[i] = this.tiles[i].getPos().toLong();
            foreign[i] = IICFluidPipe.is(this.tiles[i]);
        }
    }

    static FluidPipeNetwork build(TileEntityFluidPipe start) {
        World world = start.getWorld();
        List<TileEntityFluidPipe> members = new ArrayList<>();
        List<TileEntity> tiles = new ArrayList<>();
        List<IFluidHandler> handlers = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        Set<TileEntityFluidPipe> visited = new HashSet<>();
        ArrayDeque<TileEntityFluidPipe> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            TileEntityFluidPipe pipe = queue.poll();
            members.add(pipe);
            for (EnumFacing f : EnumFacing.VALUES) {
                if (!pipe.isConnected(f)) { continue; }
                BlockPos at = pipe.getPos().offset(f);
                TileEntity te = ICUtils.getExistingTileEntity(world, at);
                if (te == null || te.isInvalid()) { continue; }
                if (te instanceof TileEntityFluidPipe) {
                    TileEntityFluidPipe next = (TileEntityFluidPipe)te;
                    if (next.isConnected(f.getOpposite()) && visited.add(next)) { queue.add(next); }
                    continue;
                }
                if (seen.contains(at)) { continue; }
                IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
                if (handler == null) { continue; }
                seen.add(at);
                tiles.add(te);
                handlers.add(handler);
            }
        }
        FluidPipeNetwork net = new FluidPipeNetwork(world, tiles, handlers);
        for (TileEntityFluidPipe member : members) { member.network = net; }
        return net;
    }

    boolean isValid() { return valid; }

    void invalidate() { valid = false; }

    int fill(FluidStack resource, long origin, boolean doFill) {
        if (busy || !valid || resource == null || resource.amount <= 0 || size == 0) { return 0; }
        busy = true;
        try { return transfer(resource, origin, doFill); }
        finally { busy = false; }
    }

    private int transfer(FluidStack resource, long origin, boolean doFill) {
        boolean pressurized = IICFluidPipe.isPressurized(resource) || ICPipes.isNormallyPressurized(resource.getFluid());
        int limit = Math.min(resource.amount, ICPipes.getTransferableAmount(pressurized));
        if (keep == null || !keep.isFluidEqual(resource)) {
            keep = resource.copy();
            strip = ICUtils.copyFluidStackWithAmount(resource, limit, true);
        }
        int total = 0;
        int open = 0;
        for (int i = 0; i < size; i++) {
            accept[i] = 0;
            given[i] = 0;
            TileEntity tile = tiles[i];
            if (positions[i] == origin || !world.isBlockLoaded(tile.getPos())) { continue; }
            if (tile.isInvalid() || ICUtils.getExistingTileEntity(world, tile.getPos()) != tile) {
                valid = false;
                continue;
            }
            FluidStack probe = foreign[i] ? keep : strip;
            probe.amount = limit;
            int taken = Math.min(handlers[i].fill(probe, false), limit);
            if (taken <= 0) { continue; }
            accept[i] = taken;
            total += taken;
            open++;
        }
        if (total == 0) { return 0; }
        int amount = Math.min(limit, total);
        if (ICPipes.lastServed()) { serveLast(amount); }
        else { spread(amount, open); }
        if (!doFill) { return amount; }
        int filled = 0;
        for (int i = 0; i < size; i++) { if (given[i] > 0) { filled += handlers[i].fill(ICUtils.copyFluidStackWithAmount(resource, given[i], !foreign[i]), true); } }
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
