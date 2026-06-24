package me.deadybbb.customzones.events.entity;

import me.deadybbb.customzones.zone.Zone;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Triggered on {@link org.bukkit.event.block.BlockBreakEvent} in the zone
 */
public class ZonePlayerBreakBlockEvent extends ZoneEntityEvent {
    private final Block block;

    public ZonePlayerBreakBlockEvent(@NotNull Zone zone, @NotNull UUID uuid, @NotNull Block block) {
        super(zone, uuid);
        this.block = block;
    }

    /**
     * Gets the block involved in this event
     *
     * @return the block which block is involved in this event
     */
    @NotNull
    public Block getBlock() {
        return block;
    }
}
