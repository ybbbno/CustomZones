package me.deadybbb.customzones.events;

import me.deadybbb.customzones.Zone;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ZoneSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final UUID uuid;
    private final CreatureSpawnEvent.SpawnReason spawnReason;

    public ZoneSpawnEvent(Zone zone, UUID uuid, CreatureSpawnEvent.SpawnReason spawnReason) {
        this.zone = zone;
        this.uuid = uuid;
        this.spawnReason = spawnReason;
    }

    public Zone getZone() {
        return zone;
    }

    public UUID getEntityUUID() {
        return uuid;
    }

    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() { return handlers; }
}
