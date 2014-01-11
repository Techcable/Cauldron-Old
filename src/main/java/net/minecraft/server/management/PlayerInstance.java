package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class PlayerInstance
{
    public List playersInChunk = new ArrayList(); // MCPC+ - private -> public
    // JAVADOC FIELD $$ field_73264_c
    private final ChunkCoordIntPair chunkLocation;
    private short[] field_151254_d = new short[64];
    private int numberOfTilesToUpdate;
    // JAVADOC FIELD $$ field_73260_f
    private int flagsYAreasToUpdate;
    // JAVADOC FIELD $$ field_111198_g
    private long previousWorldTime;
    final PlayerManager thePlayerManager;
    private boolean loaded = false; // CraftBukkit
    private static final String __OBFID = "CL_00001435";

    public PlayerInstance(PlayerManager par1PlayerManager, int par2, int par3)
    {
        this.thePlayerManager = par1PlayerManager;
        this.playersInChunk = new ArrayList();
        this.field_151254_d = new short[64];
        this.chunkLocation = new ChunkCoordIntPair(par2, par3);
        // CraftBukkit start
        par1PlayerManager.getWorldServer().theChunkProviderServer.getChunkAt(par2, par3, new Runnable()
        {
            public void run()
            {
                PlayerInstance.this.loaded = true;
            }
        });
        // CraftBukkit end
    }

    public void addPlayer(final EntityPlayerMP par1EntityPlayerMP)   // CraftBukkit - added final to argument
    {
        if (this.playersInChunk.contains(par1EntityPlayerMP))
        {
            throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
        }
        else
        {
            if (this.playersInChunk.isEmpty())
            {
                this.previousWorldTime = this.thePlayerManager.getWorldServer().getTotalWorldTime();
            }

            this.playersInChunk.add(par1EntityPlayerMP);
            // CraftBukkit start
            if (this.loaded)
            {
                par1EntityPlayerMP.loadedChunks.add(this.chunkLocation);
            }
            else
            {
                this.thePlayerManager.getWorldServer().theChunkProviderServer.getChunkAt(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, new Runnable()
                {
                    public void run()
                    {
                        par1EntityPlayerMP.loadedChunks.add(PlayerInstance.this.chunkLocation);
                    }
                });
            }

            // CraftBukkit end
        }
    }

    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.playersInChunk.contains(par1EntityPlayerMP))
        {
            Chunk chunk = this.thePlayerManager.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);

            if (chunk.func_150802_k())
            {
                par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S21PacketChunkData(chunk, true, 0));
            }

            this.playersInChunk.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.remove(this.chunkLocation);

            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(chunkLocation, par1EntityPlayerMP));

            if (this.playersInChunk.isEmpty())
            {
                long i = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
                this.increaseInhabitedTime(chunk);
                PlayerManager.getChunkWatchers(this.thePlayerManager).remove(i);
                PlayerManager.getChunkWatcherList(this.thePlayerManager).remove(this);

                if (this.numberOfTilesToUpdate > 0)
                {
                    PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).remove(this);
                }

                this.thePlayerManager.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
            }
        }
    }

    // JAVADOC METHOD $$ func_111194_a
    public void processChunk()
    {
        this.increaseInhabitedTime(this.thePlayerManager.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));
    }

    // JAVADOC METHOD $$ func_111196_a
    private void increaseInhabitedTime(Chunk par1Chunk)
    {
        par1Chunk.inhabitedTime += this.thePlayerManager.getWorldServer().getTotalWorldTime() - this.previousWorldTime;
        this.previousWorldTime = this.thePlayerManager.getWorldServer().getTotalWorldTime();
    }

    public void func_151253_a(int p_151253_1_, int p_151253_2_, int p_151253_3_)
    {
        if (this.numberOfTilesToUpdate == 0)
        {
            PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).add(this);
        }

        this.flagsYAreasToUpdate |= 1 << (p_151253_2_ >> 4);

        //if (this.numberOfTilesToUpdate < 64) //Forge; Cache everything, so always run
        {
            short short1 = (short)(p_151253_1_ << 12 | p_151253_3_ << 8 | p_151253_2_);

            for (int l = 0; l < this.numberOfTilesToUpdate; ++l)
            {
                if (this.field_151254_d[l] == short1)
                {
                    return;
                }
            }

            if (numberOfTilesToUpdate == field_151254_d.length)
            {
                field_151254_d = Arrays.copyOf(field_151254_d, field_151254_d.length << 1);
            }
            this.field_151254_d[this.numberOfTilesToUpdate++] = short1;
        }
    }

    public void func_151251_a(Packet p_151251_1_)
    {
        for (int i = 0; i < this.playersInChunk.size(); ++i)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playersInChunk.get(i);

            if (!entityplayermp.loadedChunks.contains(this.chunkLocation))
            {
                entityplayermp.playerNetServerHandler.func_147359_a(p_151251_1_);
            }
        }
    }

    public void sendChunkUpdate()
    {
        if (this.numberOfTilesToUpdate != 0)
        {
            int i;
            int j;
            int k;

            if (this.numberOfTilesToUpdate == 1)
            {
                i = this.chunkLocation.chunkXPos * 16 + (this.field_151254_d[0] >> 12 & 15);
                j = this.field_151254_d[0] & 255;
                k = this.chunkLocation.chunkZPos * 16 + (this.field_151254_d[0] >> 8 & 15);
                this.func_151251_a(new S23PacketBlockChange(i, j, k, this.thePlayerManager.getWorldServer()));

                if (this.thePlayerManager.getWorldServer().func_147439_a(i, j, k).hasTileEntity(this.thePlayerManager.getWorldServer().getBlockMetadata(i, j, k)))
                {
                    this.func_151252_a(this.thePlayerManager.getWorldServer().func_147438_o(i, j, k));
                }
            }
            else
            {
                int l;

                if (this.numberOfTilesToUpdate == ForgeModContainer.clumpingThreshold)
                {
                    i = this.chunkLocation.chunkXPos * 16;
                    j = this.chunkLocation.chunkZPos * 16;
                    this.func_151251_a(new S21PacketChunkData(this.thePlayerManager.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), (this.flagsYAreasToUpdate == 0xFFFF), this.flagsYAreasToUpdate)); // CraftBukkit - send everything (including biome) if all sections flagged

                    /* Forge: Grabs ALL tile entities is costly on a modded server, only send needed ones
                    for (k = 0; k < 16; ++k)
                    {
                        if ((this.flagsYAreasToUpdate & 1 << k) != 0)
                        {
                            l = k << 4;
                            List list = PlayerManager.this.theWorldServer.func_147486_a(i, l, j, i + 16, l + 16, j + 16);

                            for (int i1 = 0; i1 < list.size(); ++i1)
                            {
                                this.func_151252_a((TileEntity)list.get(i1));
                            }
                        }
                    }
                    */
                }
                else
                {
                    this.func_151251_a(new S22PacketMultiBlockChange(this.numberOfTilesToUpdate, this.field_151254_d, this.thePlayerManager.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos)));
                }
                
                { //Forge: Send only the tile entities that are updated, Adding this brace lets us keep the indent and the patch small
                    WorldServer world = this.thePlayerManager.getWorldServer();
                    for (i = 0; i < this.numberOfTilesToUpdate; ++i)
                    {
                        j = this.chunkLocation.chunkXPos * 16 + (this.field_151254_d[i] >> 12 & 15);
                        k = this.field_151254_d[i] & 255;
                        l = this.chunkLocation.chunkZPos * 16 + (this.field_151254_d[i] >> 8 & 15);

                        if (world.func_147439_a(j, k, l).hasTileEntity(world.getBlockMetadata(j, k, l)))
                        {
                            this.func_151252_a(this.thePlayerManager.getWorldServer().func_147438_o(j, k, l));
                        }
                    }
                }
            }

            this.numberOfTilesToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
        }
    }

    private void func_151252_a(TileEntity p_151252_1_)
    {
        if (p_151252_1_ != null)
        {
            Packet packet = p_151252_1_.func_145844_m();

            if (packet != null)
            {
                this.func_151251_a(packet);
            }
        }
    }

    // MCPC+ start
    static ChunkCoordIntPair getChunkLocation(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.chunkLocation;
    }

    static List getPlayersInChunk(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.playersInChunk;
    }
    // MCPC+ end
}
