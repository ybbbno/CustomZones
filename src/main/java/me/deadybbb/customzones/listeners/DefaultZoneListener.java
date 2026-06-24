package me.deadybbb.customzones.listeners;

import me.deadybbb.customzones.events.ZoneEventDispatcher;
import me.deadybbb.customzones.zone.Zone;
import me.deadybbb.customzones.zone.ZoneManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DefaultZoneListener implements Listener {
    private final ZoneManager handler;
    private final ZoneEventDispatcher dispatcher;

    public DefaultZoneListener(ZoneManager handler) {
        this.handler = handler;
        this.dispatcher = handler.getDispatcher();
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (event.isCancelled()) return;

        Hanging hanging = event.getEntity();
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockFace blockFace = event.getBlockFace();
        EquipmentSlot hand = event.getHand();
        ItemStack itemStack = event.getItemStack();

        for (Zone zone : handler.getZonesAtLocation(block.getLocation())) {
            if (dispatcher.onZoneHangingPlace(zone, hanging, player, block, blockFace, hand, itemStack).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.isCancelled()) return;

        Hanging hanging = event.getEntity();
        HangingBreakEvent.RemoveCause cause = event.getCause();
        for (Zone zone : handler.getZonesAtLocation(hanging.getLocation())) {
            if (dispatcher.onZoneHangingBreak(zone, hanging, cause).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlockPlaced();

        for (Zone zone : handler.getZonesAtLocation(block.getLocation())) {
            if (dispatcher.onZonePlayerPlaceBlock(zone, uuid, block).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlock();

        for (Zone zone : handler.getZonesAtLocation(block.getLocation())) {
            if (dispatcher.onZonePlayerBreakBlock(zone, uuid, block).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        UUID uuid = event.getEntity().getUniqueId();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        for (Zone zone : handler.getZonesAtLocation(event.getLocation())) {
            if (dispatcher.onZoneCreatureSpawn(zone, uuid, reason).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        for (Zone zone : handler.getZonesAtLocation(player.getLocation())) {
            handler.triggerEnter(uuid, zone);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUniqueId();

        for(Zone zone : handler.getZonesAtLocation(entity.getLocation())) {
            handler.triggerExit(uuid, zone);
        }
    }
}
