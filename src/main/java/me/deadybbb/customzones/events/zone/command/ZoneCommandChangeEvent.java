package me.deadybbb.customzones.events.zone.command;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when the zone boundaries was changed with a command
 */
public class ZoneCommandChangeEvent extends ZoneEvent {
    public ZoneCommandChangeEvent(@NotNull Zone zone) {
        super(zone);
    }
}
