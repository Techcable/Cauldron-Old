package org.bukkit.craftbukkit.chunkio;


import org.bukkit.Server;
import org.bukkit.craftbukkit.util.AsynchronousExecutor;
import org.bukkit.craftbukkit.util.LongHash;

import java.util.concurrent.atomic.AtomicInteger;

// MCPC+ start - Don't call ChunkDataEvent.Load async
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
// MCPC+ end

class ChunkIOProvider implements AsynchronousExecutor.CallBackProvider<QueuedChunk, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException> {
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    // async stuff
    public net.minecraft.world.chunk.Chunk callStage1(QueuedChunk queuedChunk) throws RuntimeException {
        net.minecraft.world.chunk.storage.AnvilChunkLoader loader = queuedChunk.loader;
        Object[] data = loader.loadChunk__Async_CB(queuedChunk.world, queuedChunk.x, queuedChunk.z); // MCPC+

        if (data != null) {
            queuedChunk.compound = (net.minecraft.nbt.NBTTagCompound) data[1];
            return (net.minecraft.world.chunk.Chunk) data[0];
        }

        return null;
    }

    // sync stuff
    public void callStage2(QueuedChunk queuedChunk, net.minecraft.world.chunk.Chunk chunk) throws RuntimeException {
        if(chunk == null) {
            // If the chunk loading failed just do it synchronously (may generate)
            queuedChunk.provider.originalGetChunkAt(queuedChunk.x, queuedChunk.z);
            return;
        }

        queuedChunk.loader.loadEntities(chunk, queuedChunk.compound.getCompoundTag("Level"), queuedChunk.world);
        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, queuedChunk.compound)); // MCPC+ - Don't call ChunkDataEvent.Load async
        chunk.lastSaveTime = queuedChunk.provider.worldObj.getTotalWorldTime();
        queuedChunk.provider.loadedChunkHashMap.put(LongHash.toLong(queuedChunk.x, queuedChunk.z), chunk);
        queuedChunk.provider.loadedChunks.add(chunk); // MCPC+  vanilla compatibility        
        chunk.onChunkLoad();

        if (queuedChunk.provider.currentChunkProvider != null) {
            queuedChunk.provider.currentChunkProvider.recreateStructures(queuedChunk.x, queuedChunk.z);
        }

        Server server = queuedChunk.provider.worldObj.getServer();
        if (server != null) {
            server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, false));
        }

        chunk.populateChunk(queuedChunk.provider, queuedChunk.provider, queuedChunk.x, queuedChunk.z);
    }

    public void callStage3(QueuedChunk queuedChunk, net.minecraft.world.chunk.Chunk chunk, Runnable runnable) throws RuntimeException {
        runnable.run();
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "Chunk I/O Executor Thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}