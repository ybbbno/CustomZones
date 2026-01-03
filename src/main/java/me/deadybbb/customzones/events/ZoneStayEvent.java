package me.deadybbb.customzones.events;

import me.deadybbb.customzones.Zone;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ZoneStayEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final UUID uuid;
    private final int currentTicks;

    public ZoneStayEvent(Zone zone, UUID uuid, int currentTicks) {
        this.zone = zone;
        this.uuid = uuid;
        this.currentTicks = currentTicks;
    }

    public Zone getZone() {
        return zone;
    }

    public UUID getEntityUUID() {
        return uuid;
    }

    public int getCurrentTicks() { return currentTicks; }

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
