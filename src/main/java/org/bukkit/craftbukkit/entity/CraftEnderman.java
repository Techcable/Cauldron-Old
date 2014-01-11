package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

public class CraftEnderman extends CraftMonster implements Enderman {
    public CraftEnderman(CraftServer server, net.minecraft.entity.monster.EntityEnderman entity) {
        super(server, entity);
    }

    public MaterialData getCarriedMaterial() {
        return CraftMagicNumbers.getMaterial(getHandle().func_146080_bZ()).getNewData((byte) getHandle().getCarryingData());
    }

    public void setCarriedMaterial(MaterialData data) {
        getHandle().func_146081_a(CraftMagicNumbers.getBlock(data.getItemTypeId()));
        getHandle().setCarryingData(data.getData());
    }

    @Override
    public net.minecraft.entity.monster.EntityEnderman getHandle() {
        return (net.minecraft.entity.monster.EntityEnderman) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderman";
    }

    public EntityType getType() {
        return EntityType.ENDERMAN;
    }
}
