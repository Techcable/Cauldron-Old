package net.minecraftforge.cauldron.configuration;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import net.minecraftforge.cauldron.configuration.BoolSetting;

public class CauldronWorldConfig
{
    private final String worldName;
    public YamlConfiguration config;
    private boolean verbose;
    public boolean entitiesDespawnImmediate = true;

    public CauldronWorldConfig(String worldName)
    {
        this.worldName = worldName.toLowerCase();
        this.config = CauldronConfig.config;
        if (worldName.toLowerCase().contains("dummy")) return;
    }

    public void save()
    {
        CauldronConfig.save();
    }

    private void log(String s)
    {
        if ( verbose )
        {
            Bukkit.getLogger().info( s );
        }
    }

    public void set(String path, Object val)
    {
        config.set( path, val );
    }

    public boolean isBoolean(String path)
    {
        return config.isBoolean(path);
    }

    public boolean getBoolean(String path, boolean def)
    {
        if (CauldronConfig.settings.get("world-settings.default." + path) == null)
        {
            CauldronConfig.settings.put("world-settings.default." + path, new BoolSetting("world-settings.default." + path, def, ""));
        }

        config.addDefault( "world-settings.default." + path, def );
        return config.getBoolean( "world-settings." + worldName + "." + path, config.getBoolean( "world-settings.default." + path ) );
    }

    private double getDouble(String path, double def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getDouble( "world-settings." + worldName + "." + path, config.getDouble( "world-settings.default." + path ) );
    }

    public int getInt(String path, int def)
    {
        if (CauldronConfig.settings.get("world-settings.default." + path) == null)
        {
            CauldronConfig.settings.put("world-settings.default." + path, new IntSetting("world-settings.default." + path, def, ""));
        }

        config.addDefault( "world-settings.default." + path, def );
        return config.getInt( "world-settings." + worldName + "." + path, config.getInt( "world-settings.default." + path ) );
    }

    private <T> List getList(String path, T def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return (List<T>) config.getList( "world-settings." + worldName + "." + path, config.getList( "world-settings.default." + path ) );
    }

    private String getString(String path, String def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getString( "world-settings." + worldName + "." + path, config.getString( "world-settings.default." + path ) );
    }
}
