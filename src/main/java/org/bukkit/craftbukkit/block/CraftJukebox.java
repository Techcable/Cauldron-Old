package org.bukkit.craftbukkit.block;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class CraftJukebox extends CraftBlockState implements Jukebox {
    private final CraftWorld world;
    private final net.minecraft.block.BlockJukebox.TileEntityJukebox jukebox;

    public CraftJukebox(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        jukebox = (net.minecraft.block.BlockJukebox.TileEntityJukebox) world.getTileEntityAt(getX(), getY(), getZ());
    }

    @Override
    public Material getPlaying() {
        net.minecraft.item.ItemStack record = jukebox.func_145856_a();
        if (record == null) {
            return Material.AIR;
        }
        return CraftMagicNumbers.getMaterial(record.getItem());
    }

    @Override
    public void setPlaying(Material record) {
        if (record == null || CraftMagicNumbers.getItem(record) == null) {
            record = Material.AIR;
            jukebox.func_145857_a(null);
        } else {
            jukebox.func_145857_a(new net.minecraft.item.ItemStack(CraftMagicNumbers.getItem(record), 1));
        }
        jukebox.onInventoryChanged();
        if (record == Material.AIR) {
            world.getHandle().setBlockMetadataWithNotify(getX(), getY(), getZ(), 0, 3);
        } else {
            world.getHandle().setBlockMetadataWithNotify(getX(), getY(), getZ(), 1, 3);
        }
        world.playEffect(getLocation(), Effect.RECORD_PLAY, record.getId());
    }

    public boolean isPlaying() {
        return getRawData() == 1;
    }

    public boolean eject() {
        boolean result = isPlaying();
        ((net.minecraft.block.BlockJukebox) net.minecraft.init.Blocks.jukebox).func_149925_e(world.getHandle(), getX(), getY(), getZ());
        return result;
    }
}
