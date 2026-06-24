package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered when an entity exits the zone
 */
public class ZoneEntityExitEvent extends ZoneEntityEvent {
    private final int ticksSpent;

    public ZoneEntityExitEvent(@NotNull Zone zone, @NotNull UUID uuid, int ticksSpent) {
        super(zone, uuid);
        this.ticksSpent = ticksSpent;
    }

    /**
     * Gets how many ticks the entity was in the zone
     *
     * @return how many ticks the entity was in the zone
     */
    public int getTicksSpent() { return ticksSpent; }
}
