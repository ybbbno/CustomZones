package me.deadybbb.customzones.listeners;

import me.deadybbb.customzones.Zone;
import me.deadybbb.customzones.ZoneHandler;
import me.deadybbb.customzones.events.ZoneSpawnEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class DefaultZoneListener implements Listener {
    private final ZoneHandler handler;

    public DefaultZoneListener(ZoneHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        for (Zone zone : handler.getZonesAtLocation(event.getLocation())) {
            if (handler.getDispatcher().onZoneSpawn(zone, event.getEntity().getUniqueId(), event.getSpawnReason()).isCancelled()) {
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
