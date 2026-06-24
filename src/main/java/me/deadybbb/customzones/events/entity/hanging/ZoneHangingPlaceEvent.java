package me.deadybbb.customzones.events.entity.hanging;

import me.deadybbb.customzones.events.ZoneEvent;
import me.deadybbb.customzones.zone.Zone;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Triggered on {@link org.bukkit.event.hanging.HangingPlaceEvent} in the zone
 */
public class ZoneHangingPlaceEvent extends ZoneEvent {
    private final Hanging hanging;
    private final Player player;
    private final Block block;
    private final BlockFace blockFace;
    private final EquipmentSlot hand;
    private final ItemStack itemStack;

    public ZoneHangingPlaceEvent(@NotNull Zone zone, @NotNull Hanging hanging, @Nullable final Player player, @NotNull final Block block, @NotNull final BlockFace blockFace, @Nullable final EquipmentSlot hand, @Nullable ItemStack itemStack) {
        super(zone);

        this.hanging = hanging;
        this.player = player;
        this.block = block;
        this.blockFace = blockFace;
        this.hand = hand;
        this.itemStack = itemStack;
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
     * Returns the player placing the hanging entity
     *
     * @return the player placing the hanging entity
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the block that the hanging entity was placed on
     *
     * @return the block that the hanging entity was placed on
     */
    @NotNull
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the face of the block that the hanging entity was placed on
     *
     * @return the face of the block that the hanging entity was placed on
     */
    @NotNull
    public BlockFace getBlockFace() {
        return blockFace;
    }

    /**
     * Returns the hand that was used to place the hanging entity, or null
     * if a player did not place the hanging entity.
     *
     * @return the hand
     */
    @Nullable
    public EquipmentSlot getHand() {
        return hand;
    }

    /**
     * Gets the item from which the hanging entity originated
     *
     * @return the item from which the hanging entity originated
     */
    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }
}
