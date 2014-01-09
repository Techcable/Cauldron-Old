package org.bukkit.craftbukkit;


import net.minecraft.block.material.Material;

import org.bukkit.BlockChangeDelegate;

public class CraftBlockChangeDelegate {
    private final BlockChangeDelegate delegate;

    public CraftBlockChangeDelegate(BlockChangeDelegate delegate) {
        this.delegate = delegate;
    }

    public BlockChangeDelegate getDelegate() {
        return delegate;
    }

    public net.minecraft.block.Block getType(int x, int y, int z) {
        return net.minecraft.block.Block.func_149729_e(this.delegate.getTypeId(x, y, z));
    }

    public void setTypeAndData(int x, int y, int z, net.minecraft.block.Block block, int data, int updateFlag) {
        // Layering violation :(
        if (delegate instanceof net.minecraft.world.World) {
            ((net.minecraft.world.World) delegate).func_147465_d(x, y, z, block, data, 2);
        } else {
            delegate.setRawTypeIdAndData(x, y, z, net.minecraft.block.Block.func_149682_b(block), data);
        }
    }

    public boolean isEmpty(int x, int y, int z) {
        return delegate.isEmpty(x, y, z);
    }
}
