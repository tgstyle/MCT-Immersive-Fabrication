package mctmods.immersivefabrication.common.util;

import com.immersiveconvergence.api.block.ICBlockBase;
import com.immersiveconvergence.api.block.ICItemBlockBase;

public final class SlabFamily<E extends Enum<E> & ICBlockBase.IBlockEnum> {
    public final ICBlockBase<E> full;
    public final SlabBlock<E> bottom;
    public final SlabBlock<E> top;

    public SlabFamily(ICBlockBase<E> full, String name) {
        this.full = full;
        this.bottom = new SlabBlock<>(this, name, false, SlabItem.class);
        this.top = new SlabBlock<>(this, name + "_top", true, ICItemBlockBase.class);
    }
}
