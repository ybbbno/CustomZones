package me.deadybbb.customzones.events.zone;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when the zone was removed
 */
public class ZoneRemoveEvent extends ZoneEvent {
    public ZoneRemoveEvent(@NotNull Zone zone) {
        super(zone);
    }
}
