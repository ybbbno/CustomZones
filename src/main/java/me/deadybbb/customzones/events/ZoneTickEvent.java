package me.deadybbb.customzones.events;

import me.deadybbb.customzones.Zone;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZoneTickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final List<LivingEntity> entitiesInZone;

    public ZoneTickEvent(Zone zone, List<LivingEntity> entitiesInZone) {
        this.zone = zone;
        this.entitiesInZone = entitiesInZone;
    }

    public Zone getZone() {
        return zone;
    }

    public List<LivingEntity> getEntitiesInZone() {
        return entitiesInZone;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
