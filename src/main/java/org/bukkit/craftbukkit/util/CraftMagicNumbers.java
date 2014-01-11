package org.bukkit.craftbukkit.util;

import org.bukkit.Material;

public final class CraftMagicNumbers {
    private CraftMagicNumbers() {}

    public static net.minecraft.block.Block getBlock(org.bukkit.block.Block block) {
        return getBlock(block.getType());
    }

    @Deprecated
    // A bad method for bad magic.
    public static net.minecraft.block.Block getBlock(int id) {
        return getBlock(Material.getMaterial(id));
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(net.minecraft.block.Block block) {
        return net.minecraft.block.Block.func_149682_b(block);
    }

    public static Material getMaterial(net.minecraft.block.Block block) {
        return Material.getMaterial(net.minecraft.block.Block.func_149682_b(block));
    }

    public static net.minecraft.item.Item getItem(Material material) {
        // TODO: Don't use ID
        net.minecraft.item.Item item = net.minecraft.item.Item.func_150899_d(material.getId());
        return item;
    }

    @Deprecated
    // A bad method for bad magic.
    public static net.minecraft.item.Item getItem(int id) {
        return net.minecraft.item.Item.func_150899_d(id);
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(net.minecraft.item.Item item) {
        return net.minecraft.item.Item.func_150891_b(item);
    }

    public static Material getMaterial(net.minecraft.item.Item item) {
        // TODO: Don't use ID
        Material material = Material.getMaterial(net.minecraft.item.Item.func_150891_b(item));

        if (material == null) {
            return Material.AIR;
        }

        return material;
    }

    public static net.minecraft.block.Block getBlock(Material material) {
        // TODO: Don't use ID
        net.minecraft.block.Block block = net.minecraft.block.Block.func_149729_e(material.getId());

        if (block == null) {
            return net.minecraft.init.Blocks.air;
        }

        return block;
    }
}
