package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered on {@link org.bukkit.event.entity.CreatureSpawnEvent} in the zone
 */
public class ZoneCreatureSpawnEvent extends ZoneEntityEvent {
    private final CreatureSpawnEvent.SpawnReason spawnReason;

    public ZoneCreatureSpawnEvent(@NotNull Zone zone, @NotNull UUID uuid, @NotNull CreatureSpawnEvent.SpawnReason spawnReason) {
        super(zone, uuid);
        this.spawnReason = spawnReason;
    }

    /**
     * Gets the reason for why the creature is being spawned.
     *
     * @return a SpawnReason value detailing the reason for the creature being spawned
     */
    @NotNull
    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }
}