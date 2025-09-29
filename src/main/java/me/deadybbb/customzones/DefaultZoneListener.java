package me.deadybbb.customzones;

import me.deadybbb.customzones.events.ZoneStayEvent;
import me.deadybbb.customzones.events.ZoneTickEvent;
import me.deadybbb.ybmj.LegacyTextHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DefaultZoneListener implements Listener {
    private final PluginProvider plugin;

    public DefaultZoneListener(PluginProvider plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZoneStay(ZoneStayEvent event) {
        if (!(event.getEntity() instanceof Player player) || !event.getZone().displayEnabled) return;

        player.sendActionBar(LegacyTextHandler.parseText(
                "<green>Нахождение в зоне " + event.getZone().name + ": " + event.getCurrentTicks() / 20 + " секунд (" + event.getCurrentTicks() + " тиков)"
        ));
    }

    @EventHandler
    public void onZoneTick(ZoneTickEvent event) {
        Zone zone = event.getZone();
        if (!zone.displayEnabled || zone.min.getWorld() == null) return;

        Location min = zone.min;
        Location max = zone.max;
        double minX = Math.min(min.getX(), max.getX());
        double maxX = Math.max(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double maxY = Math.max(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxZ = Math.max(min.getZ(), max.getZ());

        for (double x = minX; x <= maxX; x += 1.0) {
            for (double z = minZ; z <= maxZ; z += 1.0) {
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, x, minY, z, 1, 0, 0, 0, 0);
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, x, maxY, z, 1, 0, 0, 0, 0);
            }
        }
        for (double y = minY; y <= maxY; y += 1.0) {
            for (double z = minZ; z <= maxZ; z += 1.0) {
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, minX, y, z, 1, 0, 0, 0, 0);
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, maxX, y, z, 1, 0, 0, 0, 0);
            }
        }
        for (double x = minX; x <= maxX; x += 1.0) {
            for (double y = minY; y <= maxY; y += 1.0) {
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, x, y, minZ, 1, 0, 0, 0, 0);
                min.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, x, y, maxZ, 1, 0, 0, 0, 0);
            }
        }
    }
}