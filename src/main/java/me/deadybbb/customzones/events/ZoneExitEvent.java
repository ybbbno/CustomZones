package me.deadybbb.customzones.events;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ZoneExitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final UUID uuid;
    private final int ticksSpent;

    public ZoneExitEvent(Zone zone, UUID uuid, int ticksSpent) {
        this.zone = zone;
        this.uuid = uuid;
        this.ticksSpent = ticksSpent;
    }

    public Zone getZone() {
        return zone;
    }

    public UUID getEntityUUID() {
        return uuid;
    }

    public int getTicksSpent() { return ticksSpent; }

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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
