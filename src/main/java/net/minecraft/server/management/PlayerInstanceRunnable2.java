package net.minecraft.server.management;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager.PlayerInstance;

public class PlayerInstanceRunnable2 implements Runnable {

    private PlayerInstance playerInstance;
    private EntityPlayerMP player;

    public PlayerInstanceRunnable2(EntityPlayerMP player, PlayerInstance playerInstance)
    {
        this.player = player;
        this.playerInstance = playerInstance;
    }

    @Override
    public void run()
    {
        player.loadedChunks.add(this.playerInstance.chunkLocation);
    }

}
