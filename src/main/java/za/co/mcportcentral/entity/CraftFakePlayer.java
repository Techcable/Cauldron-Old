package za.co.mcportcentral.entity;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftServer;

import za.co.mcportcentral.MCPCConfig;

public class CraftFakePlayer extends CraftPlayer
{
    public CraftFakePlayer(CraftServer server, EntityPlayerMP entity)
    {
        super(server, entity);
    }
}
