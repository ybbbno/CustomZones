package me.deadybbb.customzones.events.entity.hanging;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.bukkit.entity.Hanging;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered on {@link org.bukkit.event.hanging.HangingBreakEvent} in the zone
 */
public class ZoneHangingBreakEvent extends ZoneEvent {
    private final Hanging hanging;
    private final HangingBreakEvent.RemoveCause cause;

    public ZoneHangingBreakEvent(@NotNull Zone zone, @NotNull Hanging hanging, @NotNull HangingBreakEvent.RemoveCause cause) {
        super(zone);

        this.hanging = hanging;
        this.cause = cause;
    }

    /**
     * Gets the hanging entity involved in this event.
     *
     * @return the hanging entity
     */
    @NotNull
    public Hanging getEntity() {
        return hanging;
    }
    /**
     * Gets the cause for the hanging entity's removal
     *
     * @return the RemoveCause for the hanging entity's removal
     */
    @NotNull
    public HangingBreakEvent.RemoveCause getCause() {
        return cause;
    }
}
