package me.deadybbb.customzones.events;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ZonePlaceBlockEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Zone zone;
    private final UUID uuid;
    private final Block block;

    public ZonePlaceBlockEvent(Zone zone, UUID uuid, Block block) {
        this.zone = zone;
        this.uuid = uuid;
        this.block = block;
    }

    public Zone getZone() {
        return zone;
    }

    public UUID getEntityUUID() {
        return uuid;
    }

    public Block getBlock() {
        return block;
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
