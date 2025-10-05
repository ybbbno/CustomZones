package me.deadybbb.customzones.listeners;

import me.deadybbb.customzones.Zone;
import me.deadybbb.customzones.ZoneHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class DefaultZoneListener implements Listener {
    private final ZoneHandler handler;

    public DefaultZoneListener(ZoneHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        for(Zone zone : handler.getZonesAtLocation(player.getLocation())) {
            handler.triggerExitEvent(uuid, zone);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUniqueId();

        for(Zone zone : handler.getZonesAtLocation(entity.getLocation())) {
            handler.triggerExitEvent(uuid, zone);
        }
    }
}
