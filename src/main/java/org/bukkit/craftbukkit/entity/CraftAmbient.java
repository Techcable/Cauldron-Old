package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

public class CraftAmbient extends CraftLivingEntity implements Ambient {
    public CraftAmbient(CraftServer server, net.minecraft.entity.passive.EntityAmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityAmbientCreature getHandle() {
        return (net.minecraft.entity.passive.EntityAmbientCreature) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }

    public EntityType getType() {
        // Cauldron start
        EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
        // Cauldron end
    }
}
