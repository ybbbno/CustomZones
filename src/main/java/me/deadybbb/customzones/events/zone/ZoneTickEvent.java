package me.deadybbb.customzones.events.zone;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Triggered every 20 ticks of the zone's existence.
 */
public class ZoneTickEvent extends ZoneEvent {
    private final List<UUID> uuids;

    public ZoneTickEvent(Zone zone, List<UUID> uuids) {
        super(zone);
        this.uuids = uuids;
    }

    /**
     * Gets a list of entity uuids in the zone
     *
     * @return a list of entity uuids in the zone
     */
    @NotNull
    public List<UUID> getEntitiesUUIDsInZone() {
        return uuids;
    }
}
