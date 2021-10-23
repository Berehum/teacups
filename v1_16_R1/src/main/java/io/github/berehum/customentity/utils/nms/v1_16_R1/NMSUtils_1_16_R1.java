package io.github.berehum.customentity.utils.nms.v1_16_R1;

import io.github.berehum.customentity.utils.nms.CustomEntity;
import io.github.berehum.customentity.utils.nms.NMSUtils;
import net.minecraft.server.v1_16_R1.Entity;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NMSUtils_1_16_R1 implements NMSUtils {

    @Override
    public CustomEntity createWolfAlpha(Location location) {
        return new WolfAlpha_1_16_R1(this, location);
    }

    @Override
    public CustomEntity createWolfMember(Location location, String name) {
        return new WolfMember_1_16_R1(this, location, name);
    }

    public void spawnCustomEntity(CustomEntity customEntity) {
        WorldServer world = ((CraftWorld) customEntity.getEntity().getLocation().getWorld()).getHandle();
        world.addEntity((Entity) customEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
