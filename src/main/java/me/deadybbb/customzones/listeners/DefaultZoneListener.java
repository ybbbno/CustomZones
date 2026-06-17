package me.deadybbb.customzones.listeners;

import me.deadybbb.customzones.events.ZoneEventDispatcher;
import me.deadybbb.customzones.zone.Zone;
import me.deadybbb.customzones.zone.ZoneManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class DefaultZoneListener implements Listener {
    private final ZoneManager handler;
    private final ZoneEventDispatcher dispatcher;

    public DefaultZoneListener(ZoneManager handler) {
        this.handler = handler;
        this.dispatcher = handler.getDispatcher();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlockPlaced();

        for (Zone zone : handler.getZonesAtLocation(block.getLocation())) {
            if (dispatcher.onZonePlaceBlock(zone, uuid, block).isCancelled()) {
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
            if (dispatcher.onZoneSpawn(zone, uuid, reason).isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        for(Zone zone : handler.getZonesAtLocation(player.getLocation())) {
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
