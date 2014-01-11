package org.bukkit.craftbukkit.block;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.inventory.FurnaceInventory;

public class CraftFurnace extends CraftBlockState implements Furnace {
    private final net.minecraft.tileentity.TileEntityFurnace furnace;

    public CraftFurnace(final Block block) {
        super(block);

        furnace = (net.minecraft.tileentity.TileEntityFurnace) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    public FurnaceInventory getInventory() {
        return new CraftInventoryFurnace(furnace);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            furnace.onInventoryChanged();
        }

        return result;
    }

    public short getBurnTime() {
        return (short) furnace.field_145956_a;
    }

    public void setBurnTime(short burnTime) {
        furnace.field_145956_a = burnTime;
    }

    public short getCookTime() {
        return (short) furnace.field_145961_j;
    }

    public void setCookTime(short cookTime) {
        furnace.field_145961_j = cookTime;
    }
}
