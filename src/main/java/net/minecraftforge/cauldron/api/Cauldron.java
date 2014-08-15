package net.minecraftforge.cauldron.api;

import net.minecraftforge.cauldron.api.inventory.BukkitOreDictionary;

public class Cauldron {
    private static CauldronApi instance;
    public static void setInterface(CauldronApi cauldron) {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = cauldron;
    }

    public static CauldronApi getInterface() {
        return instance;
    }

    public static BukkitOreDictionary getOreDictionary() {
        return instance.getOreDictionary();
    }
}
