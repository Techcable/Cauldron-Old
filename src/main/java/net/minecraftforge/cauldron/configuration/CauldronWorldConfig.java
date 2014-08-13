package net.minecraftforge.cauldron.configuration;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.cauldron.configuration.BoolSetting;

public class CauldronWorldConfig extends WorldConfig
{
    public boolean entityDespawnImmediate = true;

    public CauldronWorldConfig(String worldName, ConfigBase configFile)
    {
        super(worldName, configFile);
        init();
    }

    public void init()
    {
        entityDespawnImmediate = getBoolean( "entity-despawn-immediate", true);
        this.save();
    }
}
