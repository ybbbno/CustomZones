package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered on {@link org.bukkit.event.player.PlayerBucketEmptyEvent} in the zone
 */
public class ZonePlayerBucketEmptyEvent extends ZoneEntityEvent {
    private final Material bucket;

    public ZonePlayerBucketEmptyEvent(@NotNull Zone zone, @NotNull Player player, @NotNull Material bucket) {
        super(zone, player.getUniqueId());
        this.bucket = bucket;
    }

    @NotNull
    public Material getBucket() {
        return bucket;
    }
}
