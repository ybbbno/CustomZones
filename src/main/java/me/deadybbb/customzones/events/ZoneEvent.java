package me.deadybbb.customzones.events;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a zone-related event.
 */
public abstract class ZoneEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;

    public ZoneEvent(@NotNull Zone zone) {
        this.zone = zone;
    }

    /**
     * Returns the zone where this event occurred
     *
     * @return the zone where this event occurred
     */
    public Zone getZone() {
        return zone;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
