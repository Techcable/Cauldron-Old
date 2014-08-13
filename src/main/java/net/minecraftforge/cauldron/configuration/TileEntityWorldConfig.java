package net.minecraftforge.cauldron.configuration;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.cauldron.configuration.BoolSetting;

public class TileEntityWorldConfig extends WorldConfig
{
    public TileEntityWorldConfig(String worldName, ConfigBase configFile)
    {
        super(worldName, configFile);
    }
}
