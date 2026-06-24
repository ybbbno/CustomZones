package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered when an entity enters the zone
 */
public class ZoneEntityEnterEvent extends ZoneEntityEvent {
    public ZoneEntityEnterEvent(@NotNull Zone zone, @NotNull UUID uuid) {
        super(zone, uuid);
    }
}
