package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered when an entity stays in the zone
 */
public class ZoneEntityStayEvent extends ZoneEntityEvent {
    private final int currentTicks;

    public ZoneEntityStayEvent(@NotNull Zone zone, @NotNull UUID uuid, int currentTicks) {
        super(zone, uuid);
        this.currentTicks = currentTicks;
    }

    /**
     * Gets how many ticks the entity in the zone
     *
     * @return how many ticks the entity in the zone
     */
    public int getCurrentTicks() { return currentTicks; }
}