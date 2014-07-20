package net.minecraftforge.cauldron.configuration;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.cauldron.configuration.BoolSetting;

public class CauldronWorldConfig
{
    private final String worldName;
    public ConfigBase configFile;
    private boolean verbose;
    public boolean entitiesDespawnImmediate = true;

    public CauldronWorldConfig(String worldName, ConfigBase configFile)
    {
        this.worldName = worldName.toLowerCase();
        this.configFile = configFile;
        if (worldName.toLowerCase().contains("dummy")) return;
    }

    public void save()
    {
        configFile.save();
    }

    private void log(String s)
    {
        if ( verbose )
        {
            MinecraftServer.getServer().logInfo( s );
        }
    }

    public void set(String path, Object val)
    {
        configFile.set( path, val );
    }

    public boolean isBoolean(String path)
    {
        return configFile.isBoolean(path);
    }

    public boolean getBoolean(String path, boolean def)
    {
        if (configFile.settings.get("world-settings.default." + path) == null)
        {
            configFile.settings.put("world-settings.default." + path, new BoolSetting(configFile, "world-settings.default." + path, def, ""));
        }

        configFile.config.addDefault( "world-settings.default." + path, def );
        return configFile.getBoolean( "world-settings." + worldName + "." + path, configFile.config.getBoolean( "world-settings.default." + path ) );
    }

    private double getDouble(String path, double def)
    {
        configFile.config.addDefault( "world-settings.default." + path, def );
        return configFile.config.getDouble( "world-settings." + worldName + "." + path, configFile.config.getDouble( "world-settings.default." + path ) );
    }

    public int getInt(String path, int def)
    {
        if (configFile.settings.get("world-settings.default." + path) == null)
        {
            configFile.settings.put("world-settings.default." + path, new IntSetting(configFile, "world-settings.default." + path, def, ""));
        }

        configFile.config.addDefault( "world-settings.default." + path, def );
        return configFile.getInt( "world-settings." + worldName + "." + path, configFile.config.getInt( "world-settings.default." + path ) );
    }

    private <T> List getList(String path, T def)
    {
        configFile.config.addDefault( "world-settings.default." + path, def );
        return (List<T>) configFile.config.getList( "world-settings." + worldName + "." + path, configFile.config.getList( "world-settings.default." + path ) );
    }

    private String getString(String path, String def)
    {
        configFile.config.addDefault( "world-settings.default." + path, def );
        return configFile.getString( "world-settings." + worldName + "." + path, configFile.config.getString( "world-settings.default." + path ) );
    }
}
