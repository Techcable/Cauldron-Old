package org.bukkit.craftbukkit.util;

import java.util.List;


import net.minecraft.nbt.NBTException;

import org.bukkit.Material;
import org.bukkit.UnsafeValues;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers implements UnsafeValues {
    public static final UnsafeValues INSTANCE = new CraftMagicNumbers();

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

    @Override
    public Material getMaterialFromInternalName(String name) {
        return getMaterial((net.minecraft.item.Item) net.minecraft.item.Item.field_150901_e.getObject(name));
    }

    @Override
    public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
        return StringUtil.copyPartialMatches(token, net.minecraft.item.Item.field_150901_e.func_148742_b(), completions);
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        try
        {
            nmsStack.setTagCompound((net.minecraft.nbt.NBTTagCompound) net.minecraft.nbt.JsonToNBT.func_150315_a(arguments));
        }
        catch (NBTException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));

        return stack;
    }
}
