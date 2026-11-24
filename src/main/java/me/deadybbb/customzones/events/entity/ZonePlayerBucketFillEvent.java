package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered on {@link org.bukkit.event.player.PlayerBucketFillEvent} in the zone
 */
public class ZonePlayerBucketFillEvent extends ZoneEntityEvent{
    private final Material bucket;

    public ZonePlayerBucketFillEvent(@NotNull Zone zone, @NotNull Player player, @NotNull Material bucket) {
        super(zone, player.getUniqueId());
        this.bucket = bucket;
    }

    @NotNull
    public Material getBucket() {
        return bucket;
    }
}
