package me.deadybbb.customzones.events.zone;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when the zone was created
 */
public class ZoneCreateEvent extends ZoneEvent {
    public ZoneCreateEvent(@NotNull Zone zone) {
        super(zone);
    }
}