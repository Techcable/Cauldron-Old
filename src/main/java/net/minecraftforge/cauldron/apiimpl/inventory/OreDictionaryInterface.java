package net.minecraftforge.cauldron.apiimpl.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.cauldron.api.inventory.BukkitOreDictionary;
import net.minecraftforge.cauldron.api.inventory.OreDictionaryEntry;
import net.minecraftforge.oredict.OreDictionary;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class OreDictionaryInterface implements BukkitOreDictionary {

    @Override
    public OreDictionaryEntry getOreEntry(String name) {
        return OreDictionaryEntry.valueOf(OreDictionary.getOreID(name));
    }

    @Override
    public List<OreDictionaryEntry> getOreEntries(ItemStack itemStack) {
        int[] ids = OreDictionary.getOreIDs(CraftItemStack.asNMSCopy(itemStack));

        ImmutableList.Builder<OreDictionaryEntry> builder = ImmutableList.builder();
        for (int id : ids) {
            builder.add(OreDictionaryEntry.valueOf(id));
        }

        return builder.build();
    }

    @Override
    public List<OreDictionaryEntry> getOreEntries(Material material) {
        return getOreEntries(new ItemStack(material));
    }

    @Override
    public String getOreName(OreDictionaryEntry entry) {
        return OreDictionary.getOreName(entry.getId());
    }

    @Override
    public List<ItemStack> getDefinitions(OreDictionaryEntry entry) {
        @SuppressWarnings("deprecation")
        List<net.minecraft.item.ItemStack> items = OreDictionary.getOres(entry.getId());

        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (net.minecraft.item.ItemStack nmsItem : items) {
            builder.add(CraftItemStack.asCraftMirror(nmsItem));
        }

        return builder.build();
    }

    @Override
    public List<String> getAllOreNames() {
        return Arrays.asList(OreDictionary.getOreNames());
    }
}
