package me.deadybbb.customzones.events;

import me.deadybbb.customzones.Zone;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ZoneExitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final LivingEntity entity;
    private final int ticksSpent;

    public ZoneExitEvent(Zone zone, LivingEntity entity, int ticksSpent) {
        this.zone = zone;
        this.entity = entity;
        this.ticksSpent = ticksSpent;
    }

    public Zone getZone() {
        return zone;
    }

    public LivingEntity getEntity() {
        return entity;
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
