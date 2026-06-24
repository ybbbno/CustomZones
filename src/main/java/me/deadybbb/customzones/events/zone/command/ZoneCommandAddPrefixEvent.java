package me.deadybbb.customzones.events.zone.command;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when a prefix added to the zone with a command
 */
public class ZoneCommandAddPrefixEvent extends ZoneEvent {
    public ZoneCommandAddPrefixEvent(@NotNull Zone zone) {
        super(zone);
    }
}