package net.minecraftforge.cauldron.api.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BukkitOreDictionary {

    public OreDictionaryEntry getOreEntry(String name);
    public List<OreDictionaryEntry> getOreEntries(ItemStack itemStack);
    public List<OreDictionaryEntry> getOreEntries(Material material);

    public String getOreName(OreDictionaryEntry entry);
    public List<ItemStack> getDefinitions(OreDictionaryEntry entry);

    public List<String> getAllOreNames();
}
