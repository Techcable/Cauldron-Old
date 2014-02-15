package org.bukkit.craftbukkit.chunkio;


class QueuedChunk {
    final int x;
    final int z;
    final net.minecraft.world.chunk.storage.AnvilChunkLoader loader;
    final net.minecraft.world.World world;
    final net.minecraft.world.gen.ChunkProviderServer provider;
    net.minecraft.nbt.NBTTagCompound compound;

    public QueuedChunk(int x, int z, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.World world, net.minecraft.world.gen.ChunkProviderServer provider) {
        this.x = x;
        this.z = z;
        this.loader = loader;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        return (x * 31 + z * 29) ^ world.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof QueuedChunk) {
            QueuedChunk other = (QueuedChunk) object;
            return x == other.x && z == other.z && world == other.world;
        }

        return false;
    }
}
