package me.deadybbb.customzones.events.zone.command;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when a prefix is removed from the zone with a command
 */
public class ZoneCommandRemovePrefixEvent extends ZoneEvent {
    public ZoneCommandRemovePrefixEvent(@NotNull Zone zone) {
        super(zone);
    }
}
