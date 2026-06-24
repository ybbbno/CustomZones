package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a zone-related event with an entity
 */
public abstract class ZoneEntityEvent extends ZoneEvent {
    private final UUID uuid;

    public ZoneEntityEvent(@NotNull Zone zone, @NotNull UUID uuid) {
        super(zone);

        this.uuid = uuid;
    }

    /**
     * Gets the UUID of the entity that in the zone
     *
     * @return the UUID of the entity that in the zone
     */
    @NotNull
    public UUID getEntityUUID() {
        return uuid;
    }
}
