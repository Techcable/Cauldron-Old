package org.bukkit.craftbukkit;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableSet;

import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.BanList;

public class CraftBanList implements org.bukkit.BanList {
    private final BanList list;

    public CraftBanList(BanList list){
        this.list = list;
    }

    @Override
    public org.bukkit.BanEntry getBanEntry(String target) {
        Validate.notNull(target, "Target cannot be null");

        if (!list.getBannedList().containsKey(target)) {
            return null;
        }

        return new CraftBanEntry((BanEntry) list.getBannedList().get(target), list);
    }

    @Override
    public org.bukkit.BanEntry addBan(String target, String reason, Date expires, String source) {
        Validate.notNull(target, "Ban target cannot be null");

        BanEntry entry = new BanEntry(target);
        entry.setBannedBy(StringUtils.isBlank(source) ? entry.getBannedBy() : source); // Use default if null/empty
        entry.setBanEndDate(expires); // Null values are interpreted as "forever"
        entry.setBanReason(StringUtils.isBlank(reason) ? entry.getBanReason() : reason); // Use default if null/empty

        list.put(entry);
        list.saveToFileWithHeader();
        return new CraftBanEntry(entry, list);
    }

    @Override
    public Set<org.bukkit.BanEntry> getBanEntries() {
        ImmutableSet.Builder<org.bukkit.BanEntry> builder = ImmutableSet.builder();
        for (BanEntry entry : (Collection<BanEntry>) list.getBannedList().values()) {
            builder.add(new CraftBanEntry(entry, list));
        }
        return builder.build();
    }

    @Override
    public boolean isBanned(String target) {
        Validate.notNull(target, "Target cannot be null");

        return list.isBanned(target);
    }

    @Override
    public void pardon(String target) {
        Validate.notNull(target, "Target cannot be null");

        list.remove(target);
    }

}