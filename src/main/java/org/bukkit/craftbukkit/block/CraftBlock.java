package org.bukkit.craftbukkit.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
// MCPC+ start
import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.IInventory;
import org.bukkit.craftbukkit.CraftWorld;
import za.co.mcportcentral.block.CraftCustomContainer;
// MCPC+ end

public class CraftBlock implements Block {
    private final CraftChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    // MCPC+ start - add support for custom biomes
    private static final Biome[] BIOME_MAPPING = new Biome[net.minecraft.world.biome.BiomeGenBase.getBiomeGenArray().length];
    private static final net.minecraft.world.biome.BiomeGenBase[] BIOMEBASE_MAPPING = new net.minecraft.world.biome.BiomeGenBase[net.minecraft.world.biome.BiomeGenBase.getBiomeGenArray().length];
    // MCPC+ end
    
    public CraftBlock(CraftChunk chunk, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk;
    }

    private net.minecraft.block.Block getNMSBlock() {
        return CraftMagicNumbers.getBlock(this); // TODO: UPDATE THIS
    }

    private static net.minecraft.block.Block getNMSBlock(int type) {
        return CraftMagicNumbers.getBlock(type);
    }

    public World getWorld() {
        return chunk.getWorld();
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(x);
            loc.setY(y);
            loc.setZ(z);
            loc.setYaw(0);
            loc.setPitch(0);
        }

        return loc;
    }

    public BlockVector getVector() {
        return new BlockVector(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setData(final byte data) {
        chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 3);
    }

    public void setData(final byte data, boolean applyPhysics) {
        if (applyPhysics) {
            chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 3);
        } else {
            chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 2);
        }
    }

    public byte getData() {
        return (byte) chunk.getHandle().getBlockMetadata(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public void setType(final Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(final int type) {
        return setTypeId(type, true);
    }

    public boolean setTypeId(final int type, final boolean applyPhysics) {
        return setTypeIdAndData(type, getData(), applyPhysics);
    }

    public boolean setTypeIdAndData(final int type, final byte data, final boolean applyPhysics) {
        if (applyPhysics) {
            return chunk.getHandle().worldObj.setBlock(x, y, z, getNMSBlock(type), data, 3);
        } else {
            boolean success = chunk.getHandle().worldObj.setBlock(x, y, z, getNMSBlock(type), data, 2);
            if (success) {
                chunk.getHandle().worldObj.markBlockForUpdate(x, y, z);
            }
            return success;
        }
    }

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    @Deprecated
    @Override
    public int getTypeId() {
        return CraftMagicNumbers.getId(chunk.getHandle().getBlock(this.x & 0xF, this.y & 0xFF, this.z & 0xF));
    }

    public byte getLightLevel() {
        return (byte) chunk.getHandle().worldObj.getBlockLightValue(this.x, this.y, this.z);
    }

    public byte getLightFromSky() {
        return (byte) chunk.getHandle().getSavedLightValue(net.minecraft.world.EnumSkyBlock.Sky, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public byte getLightFromBlocks() {
        return (byte) chunk.getHandle().getSavedLightValue(net.minecraft.world.EnumSkyBlock.Block, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }


    public Block getFace(final BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getFace(final BlockFace face, final int distance) {
        return getRelative(face, distance);
    }

    public Block getRelative(final int modX, final int modY, final int modZ) {
        return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
    }

    public Block getRelative(BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getRelative(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public BlockFace getFace(final Block block) {
        BlockFace[] values = BlockFace.values();

        for (BlockFace face : values) {
            if ((this.getX() + face.getModX() == block.getX()) &&
                (this.getY() + face.getModY() == block.getY()) &&
                (this.getZ() + face.getModZ() == block.getZ())
            ) {
                return face;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CraftBlock{" + "chunk=" + chunk + ",x=" + x + ",y=" + y + ",z=" + z + ",type=" + getType() + ",data=" + getData() + '}';
    }

    /**
     * Notch uses a 0-5 to mean DOWN, UP, NORTH, SOUTH, WEST, EAST
     * in that order all over. This method is convenience to convert for us.
     *
     * @return BlockFace the BlockFace represented by this number
     */
    public static BlockFace notchToBlockFace(int notch) {
        switch (notch) {
        case 0:
            return BlockFace.DOWN;
        case 1:
            return BlockFace.UP;
        case 2:
            return BlockFace.NORTH;
        case 3:
            return BlockFace.SOUTH;
        case 4:
            return BlockFace.WEST;
        case 5:
            return BlockFace.EAST;
        default:
            return BlockFace.SELF;
        }
    }

    public static int blockFaceToNotch(BlockFace face) {
        switch (face) {
        case DOWN:
            return 0;
        case UP:
            return 1;
        case NORTH:
            return 2;
        case SOUTH:
            return 3;
        case WEST:
            return 4;
        case EAST:
            return 5;
        default:
            return 7; // Good as anything here, but technically invalid
        }
    }

    public BlockState getState() {
        Material material = getType();
        // MCPC+ start - if null, check for TE that implements IInventory
        if (material == null)
        {
            TileEntity te = ((CraftWorld)this.getWorld()).getHandle().getTileEntity(this.getX(), this.getY(), this.getZ());
            if (te != null && te instanceof IInventory)
            {
                // In order to allow plugins to properly grab the container location, we must pass a class that extends CraftBlockState and implements InventoryHolder.
                // Note: This will be returned when TileEntity.getOwner() is called
                return new CraftCustomContainer(this);
            }
            // pass default state
            return new CraftBlockState(this);
        }
        // MCPC+ end
        switch (material) {
        case SIGN:
        case SIGN_POST:
        case WALL_SIGN:
            return new CraftSign(this);
        case CHEST:
        case TRAPPED_CHEST:
            return new CraftChest(this);
        case BURNING_FURNACE:
        case FURNACE:
            return new CraftFurnace(this);
        case DISPENSER:
            return new CraftDispenser(this);
        case DROPPER:
            return new CraftDropper(this);
        case HOPPER:
            return new CraftHopper(this);
        case MOB_SPAWNER:
            return new CraftCreatureSpawner(this);
        case NOTE_BLOCK:
            return new CraftNoteBlock(this);
        case JUKEBOX:
            return new CraftJukebox(this);
        case BREWING_STAND:
            return new CraftBrewingStand(this);
        case SKULL:
            return new CraftSkull(this);
        case COMMAND:
            return new CraftCommandBlock(this);
        case BEACON:
            return new CraftBeacon(this);
        default:
            // MCPC+ start
            TileEntity te = ((CraftWorld)this.getWorld()).getHandle().getTileEntity(this.getX(), this.getY(), this.getZ());
            if (te != null && te instanceof IInventory)
            {
                // In order to allow plugins to properly grab the container location, we must pass a class that extends CraftBlockState and implements InventoryHolder.
                // Note: This will be returned when TileEntity.getOwner() is called
                return new CraftCustomContainer(this);
            }
            // pass default state
            // MCPC+ end
            return new CraftBlockState(this);
        }
    }

    public Biome getBiome() {
        return getWorld().getBiome(x, z);
    }

    public void setBiome(Biome bio) {
        getWorld().setBiome(x, z, bio);
    }

    public static Biome biomeBaseToBiome(net.minecraft.world.biome.BiomeGenBase base) {
        if (base == null) {
            return null;
        }

        return BIOME_MAPPING[base.biomeID];
    }

    public static net.minecraft.world.biome.BiomeGenBase biomeToBiomeBase(Biome bio) {
        if (bio == null) {
            return null;
        }
        return BIOMEBASE_MAPPING[bio.ordinal()];
    }

    public double getTemperature() {
        return getWorld().getTemperature(x, z);
    }

    public double getHumidity() {
        return getWorld().getHumidity(x, z);
    }

    public boolean isBlockPowered() {
        return chunk.getHandle().worldObj.getBlockPowerInput(x, y, z) > 0;
    }

    public boolean isBlockIndirectlyPowered() {
        return chunk.getHandle().worldObj.isBlockIndirectlyGettingPowered(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CraftBlock)) return false;
        CraftBlock other = (CraftBlock) o;

        return this.x == other.x && this.y == other.y && this.z == other.z && this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        return this.y << 24 ^ this.x ^ this.z ^ this.getWorld().hashCode();
    }

    public boolean isBlockFacePowered(BlockFace face) {
        return chunk.getHandle().worldObj.getIndirectPowerOutput(x, y, z, blockFaceToNotch(face));
    }

    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        int power = chunk.getHandle().worldObj.getIndirectPowerLevelTo(x, y, z, blockFaceToNotch(face));

        Block relative = getRelative(face);
        if (relative.getType() == Material.REDSTONE_WIRE) {
            return Math.max(power, relative.getData()) > 0;
        }

        return power > 0;
    }

    public int getBlockPower(BlockFace face) {
        int power = 0;
        net.minecraft.block.BlockRedstoneWire wire = net.minecraft.init.Blocks.redstone_wire;
        net.minecraft.world.World world = chunk.getHandle().worldObj;
        if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y - 1, z, 0)) power = wire.func_150178_a(world, x, y - 1, z, power);
        if ((face == BlockFace.UP || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y + 1, z, 1)) power = wire.func_150178_a(world, x, y + 1, z, power);
        if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.getIndirectPowerOutput(x + 1, y, z, 2)) power = wire.func_150178_a(world, x + 1, y, z, power);
        if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.getIndirectPowerOutput(x - 1, y, z, 3)) power = wire.func_150178_a(world, x - 1, y, z, power);
        if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y, z - 1, 4)) power = wire.func_150178_a(world, x, y, z - 1, power);
        if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y, z + 1, 5)) power = wire.func_150178_a(world, x, y, z - 1, power);
        return power > 0 ? power : (face == BlockFace.SELF ? isBlockIndirectlyPowered() : isBlockFaceIndirectlyPowered(face)) ? 15 : 0;
    }

    public int getBlockPower() {
        return getBlockPower(BlockFace.SELF);
    }

    public boolean isEmpty() {
        // MCPC+ start - support custom air blocks (Railcraft player aura tracking block)
        //return getType() == Material.AIR;
        if (getType() == Material.AIR) return true;
        if (!(getWorld() instanceof CraftWorld)) return false;
        return ((CraftWorld) getWorld()).getHandle().isAirBlock(getX(), getY(), getZ());
        // MCPC+ end
    }

    public boolean isLiquid() {
        return (getType() == Material.WATER) || (getType() == Material.STATIONARY_WATER) || (getType() == Material.LAVA) || (getType() == Material.STATIONARY_LAVA);
    }

    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(getNMSBlock().getMaterial().getMaterialMobility());
    }

    private boolean itemCausesDrops(ItemStack item) {
        net.minecraft.block.Block block = this.getNMSBlock();
        net.minecraft.item.Item itemType = item != null ? net.minecraft.item.Item.getItemById(item.getTypeId()) : null;
        return block != null && (block.getMaterial().isToolNotRequired() || (itemType != null && itemType.func_150897_b(block)));
    }

    public boolean breakNaturally() {
        // Order matters here, need to drop before setting to air so skulls can get their data
        net.minecraft.block.Block block = this.getNMSBlock();
        byte data = getData();
        boolean result = false;

        if (block != null && block != net.minecraft.init.Blocks.air) {
            block.dropBlockAsItemWithChance(chunk.getHandle().worldObj, x, y, z, data, 1.0F, 0);
            result = true;
        }

        setTypeId(Material.AIR.getId());
        return result;
    }

    public boolean breakNaturally(ItemStack item) {
        if (itemCausesDrops(item)) {
            return breakNaturally();
        } else {
            return setTypeId(Material.AIR.getId());
        }
    }

    public Collection<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<ItemStack>();

        net.minecraft.block.Block block = this.getNMSBlock();
        if (block != net.minecraft.init.Blocks.air) {
            byte data = getData();
            // based on nms.Block.dropNaturally
            int count = block.quantityDroppedWithBonus(0, chunk.getHandle().worldObj.rand);
            for (int i = 0; i < count; ++i) {
                net.minecraft.item.Item item = block.getItemDropped(data, chunk.getHandle().worldObj.rand, 0);
                if (item != null) {
                    // Skulls are special, their data is based on the tile entity
                    if (net.minecraft.init.Blocks.skull == block) {
                        net.minecraft.item.ItemStack nmsStack = new net.minecraft.item.ItemStack(item, 1, block.getDamageValue(chunk.getHandle().worldObj, x, y, z));
                        net.minecraft.tileentity.TileEntitySkull tileentityskull = (net.minecraft.tileentity.TileEntitySkull) chunk.getHandle().worldObj.getTileEntity(x, y, z);

                        if (tileentityskull.func_145904_a() == 3 && tileentityskull.func_145907_c() != null && tileentityskull.func_145907_c().length() > 0) {
                            nmsStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                            nmsStack.getTagCompound().setString("SkullOwner", tileentityskull.func_145907_c());
                        }

                        drops.add(CraftItemStack.asBukkitCopy(nmsStack));
                        // We don't want to drop cocoa blocks, we want to drop cocoa beans.
                    } else if (net.minecraft.init.Blocks.cocoa == block) {
                        int dropAmount = (net.minecraft.block.BlockCocoa.func_149987_c(data) >= 2 ? 3 : 1);
                        for (int j = 0; j < dropAmount; ++j) {
                            drops.add(new ItemStack(Material.INK_SACK, 1, (short) 3));
                        }
                    } else {
                        drops.add(new ItemStack(org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(item), 1, (short) block.damageDropped(data)));
                    }
                }
            }
        }
        return drops;
    }

    public Collection<ItemStack> getDrops(ItemStack item) {
        if (itemCausesDrops(item)) {
            return getDrops();
        } else {
            return Collections.emptyList();
        }
    }

    /* Build biome index based lookup table for BiomeBase to Biome mapping */
    public static void initMappings() { // MCPC+ - initializer to initMappings() method; called in CraftServer
        //BIOME_MAPPING = new Biome[net.minecraft.world.biome.BiomeGenBase.biomeList.length]; // MCPC+ - move up
        //BIOMEBASE_MAPPING = new net.minecraft.world.biome.BiomeGenBase[Biome.values().length]; // MCPC+ - move up
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.swampland.biomeID] = Biome.SWAMPLAND;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.forest.biomeID] = Biome.FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.taiga.biomeID] = Biome.TAIGA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.desert.biomeID] = Biome.DESERT;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.plains.biomeID] = Biome.PLAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.hell.biomeID] = Biome.HELL;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.sky.biomeID] = Biome.SKY;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.river.biomeID] = Biome.RIVER;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHills.biomeID] = Biome.EXTREME_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.ocean.biomeID] = Biome.OCEAN;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.frozenOcean.biomeID] = Biome.FROZEN_OCEAN;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.frozenRiver.biomeID] = Biome.FROZEN_RIVER;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.icePlains.biomeID] = Biome.ICE_PLAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.iceMountains.biomeID] = Biome.ICE_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mushroomIsland.biomeID] = Biome.MUSHROOM_ISLAND;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mushroomIslandShore.biomeID] = Biome.MUSHROOM_SHORE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.beach.biomeID] = Biome.BEACH;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.desertHills.biomeID] = Biome.DESERT_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.forestHills.biomeID] = Biome.FOREST_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.taigaHills.biomeID] = Biome.TAIGA_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHillsEdge.biomeID] = Biome.SMALL_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungle.biomeID] = Biome.JUNGLE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungleHills.biomeID] = Biome.JUNGLE_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungleEdge.biomeID] = Biome.JUNGLE_EDGE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.deepOcean.biomeID] = Biome.DEEP_OCEAN;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.stoneBeach.biomeID] = Biome.STONE_BEACH;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.coldBeach.biomeID] = Biome.COLD_BEACH;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.birchForest.biomeID] = Biome.BIRCH_FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.birchForestHills.biomeID] = Biome.BIRCH_FOREST_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.roofedForest.biomeID] = Biome.ROOFED_FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.coldTaiga.biomeID] = Biome.COLD_TAIGA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.coldTaigaHills.biomeID] = Biome.COLD_TAIGA_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.megaTaiga.biomeID] = Biome.MEGA_TAIGA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.megaTaigaHills.biomeID] = Biome.MEGA_TAIGA_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHillsPlus.biomeID] = Biome.EXTREME_HILLS_PLUS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.savanna.biomeID] = Biome.SAVANNA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.savannaPlateau.biomeID] = Biome.SAVANNA_PLATEAU;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesa.biomeID] = Biome.MESA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesaPlateau_F.biomeID] = Biome.MESA_PLATEAU_FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesaPlateau.biomeID] = Biome.MESA_PLATEAU;

        // Extended Biomes
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.plains.biomeID + 128] = Biome.SUNFLOWER_PLAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.desert.biomeID + 128] = Biome.DESERT_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.forest.biomeID + 128] = Biome.FLOWER_FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.taiga.biomeID + 128] = Biome.TAIGA_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.swampland.biomeID + 128] = Biome.SWAMPLAND_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.icePlains.biomeID + 128] = Biome.ICE_PLAINS_SPIKES;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungle.biomeID + 128] = Biome.JUNGLE_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungleEdge.biomeID + 128] = Biome.JUNGLE_EDGE_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.coldTaiga.biomeID + 128] = Biome.COLD_TAIGA_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.savanna.biomeID + 128] = Biome.SAVANNA_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.savannaPlateau.biomeID + 128] = Biome.SAVANNA_PLATEAU_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesa.biomeID + 128] = Biome.MESA_BRYCE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesaPlateau_F.biomeID + 128] = Biome.MESA_PLATEAU_FOREST_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mesaPlateau.biomeID + 128] = Biome.MESA_PLATEAU_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.birchForest.biomeID + 128] = Biome.BIRCH_FOREST_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.birchForestHills.biomeID + 128] = Biome.BIRCH_FOREST_HILLS_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.roofedForest.biomeID + 128] = Biome.ROOFED_FOREST_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.megaTaiga.biomeID + 128] = Biome.MEGA_SPRUCE_TAIGA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHills.biomeID + 128] = Biome.EXTREME_HILLS_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHillsPlus.biomeID + 128] = Biome.EXTREME_HILLS_PLUS_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.megaTaigaHills.biomeID + 128] = Biome.MEGA_SPRUCE_TAIGA_HILLS;

        /* Sanity check - we should have a record for each record in the BiomeBase.a table */
        /* Helps avoid missed biomes when we upgrade bukkit to new code with new biomes */
        for (int i = 0; i < BIOME_MAPPING.length; i++) {
            if ((net.minecraft.world.biome.BiomeGenBase.getBiome(i) != null) && (BIOME_MAPPING[i] == null)) {
                // MCPC+ start - add support for mod biomes
                //throw new IllegalArgumentException("Missing Biome mapping for BiomeBase[" + i + "]");
                String name = net.minecraft.world.biome.BiomeGenBase.getBiome(i).biomeName;
                int id = net.minecraft.world.biome.BiomeGenBase.getBiome(i).biomeID;

                System.out.println("Adding biome mapping " + net.minecraft.world.biome.BiomeGenBase.getBiome(i).biomeID + " " + name + " at BiomeBase[" + i + "]");
                net.minecraftforge.common.util.EnumHelper.addBukkitBiome(name); // Forge
                BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.getBiome(i).biomeID] = ((Biome) Enum.valueOf(Biome.class, name));
                // MCPC+ end           
            }
            if (BIOME_MAPPING[i] != null) {  /* Build reverse mapping for setBiome */
                BIOMEBASE_MAPPING[BIOME_MAPPING[i].ordinal()] = net.minecraft.world.biome.BiomeGenBase.getBiome(i);
            }
        }
    }

    // MCPC+ start - if mcpc.dump-materials is true, dump all materials with their corresponding id's
    public static void dumpMaterials() {
        if (za.co.mcportcentral.MCPCConfig.Setting.dumpMaterials.getValue())
        {
            FMLLog.info("MCPC Dump Materials is ENABLED. Starting dump...");
            for (int i = 0; i < 32000; i++)
            {
                Material material = Material.getMaterial(i);
                if (material != null)
                {
                    FMLLog.info("Found material " + material + " with ID " + i);
                }
            }
            FMLLog.info("MCPC Dump Materials complete.");
            FMLLog.info("To disable these dumps, set mcpc.dump-materials to false in bukkit.yml.");
        }
    }
    // MCPC+ end

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        chunk.getCraftWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return chunk.getCraftWorld().getBlockMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return chunk.getCraftWorld().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        chunk.getCraftWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }
}
