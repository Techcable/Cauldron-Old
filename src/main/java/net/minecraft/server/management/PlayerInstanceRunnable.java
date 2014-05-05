package net.minecraft.server.management;

import net.minecraft.server.management.PlayerManager.PlayerInstance;

public class PlayerInstanceRunnable implements Runnable {

    private PlayerInstance playerInstance;

    public PlayerInstanceRunnable(PlayerInstance playerInstance)
    {
        this.playerInstance = playerInstance;
    }

    @Override
    public void run()
    {
        this.playerInstance.loaded = true;
    }

}
