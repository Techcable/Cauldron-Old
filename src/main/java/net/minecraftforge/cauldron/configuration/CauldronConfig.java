package net.minecraftforge.cauldron.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.cauldron.CauldronCommand;
import net.minecraftforge.cauldron.CauldronHooks;
import net.minecraftforge.cauldron.TileEntityCache;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Throwables;

public class CauldronConfig
{

    private static final File CONFIG_FILE = new File("cauldron.yml");
    private static final String HEADER = "This is the main configuration file for Cauldron.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to Cauldron,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #cauldron @ irc.esper.net ( http://webchat.esper.net/?channel=cauldron )\n"
            + "Forums: http://cauldron.minecraftforge.net/\n";
    
    /* ======================================================================== */

    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;

    public static Map<String, Setting> settings = new HashMap<String, Setting>();

    // Logging options
    public static final BoolSetting dumpMaterials = new BoolSetting("settings.dump-materials", false, "Dumps all materials with their corresponding id's");
    public static final BoolSetting disableWarnings = new BoolSetting("logging.disabled-warnings", false, "Disable warning messages to server admins");
    public static final BoolSetting worldLeakDebug = new BoolSetting("logging.world-leak-debug", false, "Log worlds that appear to be leaking (buggy)");
    public static final BoolSetting connectionLogging = new BoolSetting("logging.connection", false, "Log connections");
    public static final BoolSetting tileEntityPlaceLogging = new BoolSetting("logging.warn-place-no-tileentity", true, "Warn when a mod requests tile entity from a block that doesn't support one");
    public static final BoolSetting tickIntervalLogging = new BoolSetting("logging.tick-intervals", false, "Log when skip interval handlers are ticked");
    public static final BoolSetting chunkLoadLogging = new BoolSetting("logging.chunk-load", false, "Log when chunks are loaded (dev)");
    public static final BoolSetting chunkUnloadLogging = new BoolSetting("logging.chunk-unload", false, "Log when chunks are unloaded (dev)");
    public static final BoolSetting entitySpawnLogging = new BoolSetting("logging.entity-spawn", false, "Log when living entities are spawned (dev)");
    public static final BoolSetting entityDespawnLogging = new BoolSetting("logging.entity-despawn", false, "Log when living entities are despawned (dev)");
    public static final BoolSetting entityDeathLogging = new BoolSetting("logging.entity-death", false, "Log when an entity is destroyed (dev)");
    public static final BoolSetting logWithStackTraces = new BoolSetting("logging.detailed-logging", false, "Add stack traces to dev logging");
    public static final BoolSetting dumpChunksOnDeadlock = new BoolSetting("logging.dump-chunks-on-deadlock", false, "Dump chunks in the event of a deadlock (helps to debug the deadlock)");
    public static final BoolSetting dumpHeapOnDeadlock = new BoolSetting("logging.dump-heap-on-deadlock", false, "Dump the heap in the event of a deadlock (helps to debug the deadlock)");
    public static final BoolSetting dumpThreadsOnWarn = new BoolSetting("logging.dump-threads-on-warn", false, "Dump the the server thread on deadlock warning (delps to debug the deadlock)");
    public static final BoolSetting logEntityCollisionChecks = new BoolSetting("logging.entity-collision-checks", false, "Whether to log entity collision/count checks");
    public static final BoolSetting logEntitySpeedRemoval = new BoolSetting("logging.entity-speed-removal", false, "Whether to log entity removals due to speed");
    public static final IntSetting largeCollisionLogSize = new IntSetting("logging.collision-warn-size", 200, "Number of colliding entities in one spot before logging a warning. Set to 0 to disable");
    public static final IntSetting largeEntityCountLogSize = new IntSetting("logging.entity-count-warn-size", 0, "Number of entities in one dimension logging a warning. Set to 0 to disable");

    // General settings
    public static final BoolSetting loadChunkOnRequest = new BoolSetting("settings.load-chunk-on-request", true, "Forces Chunk Loading on 'Provide' requests (speedup for mods that don't check if a chunk is loaded");
    public static final BoolSetting loadChunkOnForgeTick = new BoolSetting("settings.load-chunk-on-forge-tick", false, "Forces Chunk Loading during Forge Server Tick events");
    public static final BoolSetting checkEntityBoundingBoxes = new BoolSetting("settings.check-entity-bounding-boxes", false, "Removes an entity that exceeds the max bounding box size.");
    public static final BoolSetting checkEntityMaxSpeeds = new BoolSetting("settings.check-entity-max-speeds", false, "Removes any entity that exceeds max speed.");
    public static final IntSetting largeBoundingBoxLogSize = new IntSetting("settings.entity-bounding-box-max-size", 1000, "Max size of an entity's bounding box before removing it (either being too large or bugged and 'moving' too fast)");
    public static final IntSetting entityMaxSpeed = new IntSetting("settings.entity-max-speed", 100, "Square of the max speed of an entity before removing it");

    // Debug settings
    public static final BoolSetting enableThreadContentionMonitoring = new BoolSetting("debug.thread-contention-monitoring", false, "Set true to enable Java's thread contention monitoring for thread dumps");

    // Server options
    public static final BoolSetting infiniteWaterSource = new BoolSetting("world-settings.default.infinite-water-source", true, "Vanilla water source behavior - is infinite");
    public static final BoolSetting flowingLavaDecay = new BoolSetting("world-settings.default.flowing-lava-decay", false, "Lava behaves like vanilla water when source block is removed");
    public static final BoolSetting fakePlayerLogin = new BoolSetting("fake-players.do-login", false, "Raise login events for fake players");

    // Plug-in options
    public static final BoolSetting remapPluginFile = new BoolSetting("plugin-settings.default.remap-plugin-file", false, "Remap the plugin file (dev)");

    static
    {
        settings.put(dumpMaterials.path, dumpMaterials);
        settings.put(disableWarnings.path, disableWarnings);
        settings.put(worldLeakDebug.path, worldLeakDebug);
        settings.put(connectionLogging.path, connectionLogging);
        settings.put(tileEntityPlaceLogging.path, tileEntityPlaceLogging);
        settings.put(tickIntervalLogging.path, tickIntervalLogging);
        settings.put(chunkLoadLogging.path, chunkLoadLogging);
        settings.put(chunkUnloadLogging.path, chunkUnloadLogging);
        settings.put(entitySpawnLogging.path, entitySpawnLogging);
        settings.put(entityDespawnLogging.path, entityDespawnLogging);
        settings.put(entityDeathLogging.path, entityDeathLogging);
        settings.put(logWithStackTraces.path, logWithStackTraces);
        settings.put(dumpChunksOnDeadlock.path, dumpChunksOnDeadlock);
        settings.put(dumpHeapOnDeadlock.path, dumpHeapOnDeadlock);
        settings.put(dumpThreadsOnWarn.path, dumpThreadsOnWarn);
        settings.put(logEntityCollisionChecks.path, logEntityCollisionChecks);
        settings.put(logEntitySpeedRemoval.path, logEntitySpeedRemoval);
        settings.put(largeCollisionLogSize.path, largeCollisionLogSize);
        settings.put(largeEntityCountLogSize.path, largeEntityCountLogSize);
        settings.put(loadChunkOnRequest.path, loadChunkOnRequest);
        settings.put(loadChunkOnForgeTick.path, loadChunkOnForgeTick);
        settings.put(checkEntityBoundingBoxes.path, checkEntityBoundingBoxes);
        settings.put(checkEntityMaxSpeeds.path, checkEntityMaxSpeeds);
        settings.put(largeBoundingBoxLogSize.path, largeBoundingBoxLogSize);
        settings.put(enableThreadContentionMonitoring.path, enableThreadContentionMonitoring);
        settings.put(infiniteWaterSource.path, infiniteWaterSource);
        settings.put(flowingLavaDecay.path, flowingLavaDecay);
        settings.put(fakePlayerLogin.path, fakePlayerLogin);
        settings.put(remapPluginFile.path, remapPluginFile);
    }

    /* ======================================================================== */

    public static void init()
    {
        if (config == null)
        {
            commands = new HashMap<String, Command>();
            commands.put("cauldron", new CauldronCommand());
            load(false);
        }
    }

    public static void registerCommands()
    {
        for (Map.Entry<String, Command> entry : commands.entrySet())
        {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "cauldron", entry.getValue());
        }
    }

    public static void save()
    {
        try
        {
            config.save(CONFIG_FILE);
        }
        catch (IOException ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    public static void load(boolean refreshTECache)
    {
        try
        {
            config = YamlConfiguration.loadConfiguration(CONFIG_FILE);
            String header = HEADER + "\n";
            for (Setting toggle : settings.values())
            {
                if (!toggle.description.equals(""))
                    header += "Setting: " + toggle.path + " Default: " + toggle.def + "   # " + toggle.description + "\n";

                config.addDefault(toggle.path, toggle.def);
                settings.get(toggle.path).setValue(config.getString(toggle.path));
            }
            config.options().header(header);
            config.options().copyDefaults(true);

            version = getInt("config-version", 1);
            set("config-version", 1);

            // Update config references for our world configs
            for (WorldServer world : DimensionManager.getWorlds())
            {
                if (world.cauldronConfig != null)
                {
                    world.cauldronConfig.config = config;
                    world.cauldronConfig.entitiesDespawnImmediate = world.cauldronConfig.getBoolean("entities.despawn-immediate", true);
                }
            }
            // Update TE Cache
            if (refreshTECache)
            {
                for (Map.Entry<Class<? extends TileEntity>, TileEntityCache> teEntry : CauldronHooks.tileEntityCache.entrySet())
                {
                      //  System.out.println("CONFIG RELOADED, READING TE VALUES FROM CONFIG");
                    TileEntityCache teCache = teEntry.getValue();
                    teCache.tickNoPlayers = config.getBoolean( "world-settings." + teCache.worldName + "." + teCache.configPath + ".tick-no-players", config.getBoolean( "world-settings.default." + teCache.configPath + ".tick-no-players") );
                    teCache.tickInterval = config.getInt( "world-settings." + teCache.worldName + "." + teCache.configPath + ".tick-interval", config.getInt( "world-settings.default." + teCache.configPath + ".tick-interval") );
                }
            }
            CauldronConfig.save();
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + CONFIG_FILE, ex);
        }
    }

    public static void set(String path, Object val)
    {
        config.set(path, val);
    }

    public static boolean isSet(String path)
    {
        return config.isSet(path);
    }

    public static boolean isInt(String path)
    {
        return config.isInt(path);
    }

    public static boolean isBoolean(String path)
    {
        return config.isBoolean(path);
    }

    public static boolean getBoolean(String path, boolean def)
    {
        return getBoolean(path, def, true);
    }

    public static boolean getBoolean(String path, boolean def, boolean useDefault)
    {
        if (useDefault)
        {
            config.addDefault(path, def);
        }
        return config.getBoolean(path, def);
    }

    public static int getInt(String path, int def)
    {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def)
    {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public static String getString(String path, String def)
    {
        return getString(path, def, true);
    }

    public static String getString(String path, String def, boolean useDefault)
    {
        if (useDefault)
        {
            config.addDefault(path, def);
        }
        return config.getString(path, def);
    }

    public static String getFakePlayer(String className, String defaultName)
    {
        return getString("fake-players." + className + ".username", defaultName);
    }
}
