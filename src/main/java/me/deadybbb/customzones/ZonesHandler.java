package me.deadybbb.myrosynthesis.customzone;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import me.deadybbb.myrosynthesis.customeffects.EffectsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity; // Changed from Player
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class ZonesHandler {
    private final ZonesConfigHandler configHandler;
    private final EffectsHandler effectsHandler;
    private final LegacyTextHandler textHandler;
    private final JavaPlugin plugin;

    public List<Zone> zones;
    public final Map<UUID, Location> pos1 = new HashMap<>();
    public final Map<UUID, Location> pos2 = new HashMap<>();
    final Map<UUID, BukkitRunnable> activeTasks = new HashMap<>();

    public ZonesHandler(JavaPlugin plugin, EffectsHandler effectsHandler, LegacyTextHandler textHandler) {
        configHandler = new ZonesConfigHandler(plugin);
        zones = configHandler.loadZonesFromConfig();
        this.effectsHandler = effectsHandler;
        this.textHandler = textHandler;
        this.plugin = plugin;

        startTimer(0L, 20L);
    }

    public boolean reloadZonesFromConfig() {
        try {
            zones = configHandler.loadZonesFromConfig();
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load zones config", e);
            return false;
        }
    }

    public void startEffectsTimer(LivingEntity entity, Zone zone, long delay, long period) {
        if (entity == null || zone == null || delay < 0 || period <= 0) return;
        if (!isEntityInZone(entity, zone)) return;

        UUID entityId = entity.getUniqueId();

        if (activeTasks.containsKey(entityId)) return;

        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!entity.isValid() || !isEntityInZone(entity, zone)) {
                    cancel();
                    activeTasks.remove(entityId);
                    return;
                }

                // Only send action bar to players
                if (entity instanceof Player player && zone.displayEnabled) {
                    player.sendActionBar(textHandler.parseText("<green>Нахождение в зоне: " + ticks / 20 + " секунд (" + ticks + " тиков)"));
                }

                for (String effect : zone.effects) {
                    effectsHandler.applyEffectById(entity, effect, ticks); // Updated to handle LivingEntity
                }
                ticks += (int) period;
            }
        };

        task.runTaskTimer(plugin, delay, period);
        activeTasks.put(entityId, task);
    }

    public List<String> getAllZonesNames(String zoneName) {
        return zones.stream()
                .map(zone -> zone.name)
                .filter(name -> name.toLowerCase().startsWith(zoneName.toLowerCase()))
                .toList();
    }

    public List<String> getAllEffectsNames(String effectName) {
        return effectsHandler.effects.stream()
                .map(effect -> effect.name)
                .filter(name -> name.toLowerCase().startsWith(effectName.toLowerCase()))
                .toList();
    }

    public boolean addEffectToZone(String name, String effect) {
        if (name == null || effect == null) return false;
        try {
            Zone zone = zones.stream().filter(z -> z.name.equals(name)).findFirst().orElse(null);
            if (zone == null || effectsHandler.effects.stream().noneMatch(e -> e.name.equals(effect))) {
                return false;
            }
            if (!zone.effects.contains(effect)) {
                zone.effects.add(effect);
                saveZones();
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add " + effect + " to zone " + name, e);
            return false;
        }
    }

    public boolean removeEffectFromZones(String effect) {
        if (effect == null || effect.isEmpty()) return false;
        try {
            boolean modified = false;
            for (Zone zone : zones) {
                if (zone.effects.remove(effect)) {
                    modified = true;
                }
            }
            if (modified) {
                saveZones();
            }
            return modified;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove effect " + effect + " from zones", e);
            return false;
        }
    }

    public boolean removeEffectFromZone(String name, String effect) {
        if (name == null || effect == null) return false;
        try {
            Zone zone = zones.stream().filter(z -> z.name.equals(name)).findFirst().orElse(null);
            if (zone == null) return false;
            boolean removed = zone.effects.remove(effect);
            if (removed) saveZones();
            return removed;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove effect " + effect + " from zone " + name, e);
            return false;
        }
    }

    public void startTimer(long delay, long period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Iterate over all worlds and their living entities
                for (var world : Bukkit.getWorlds()) {
                    for (LivingEntity entity : world.getLivingEntities()) {
                        for (Zone zone : zones) {
                            startEffectsTimer(entity, zone, delay, period);
                        }
                    }
                }
                displayZones();
            }
        }.runTaskTimer(plugin, delay, period);
    }

    private boolean isEntityInZone(LivingEntity entity, Zone zone) {
        Location loc = entity.getLocation();
        return loc.getWorld().equals(zone.min.getWorld()) &&
                loc.getX() >= Math.min(zone.min.getX(), zone.max.getX()) &&
                loc.getX() <= Math.max(zone.min.getX(), zone.max.getX()) &&
                loc.getY() >= Math.min(zone.min.getY(), zone.max.getY()) &&
                loc.getY() <= Math.max(zone.min.getY(), zone.max.getY()) &&
                loc.getZ() >= Math.min(zone.min.getZ(), zone.max.getZ()) &&
                loc.getZ() <= Math.max(zone.min.getZ(), zone.max.getZ());
    }

    private void displayZones() {
        for (Zone zone : zones) {
            if (!zone.displayEnabled || zone.min.getWorld() == null) continue;

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
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, minY, z, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, maxY, z, 1, 0, 0, 0, 0);
                }
            }
            for (double y = minY; y <= maxY; y += 1.0) {
                for (double z = minZ; z <= maxZ; z += 1.0) {
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, minX, y, z, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, maxX, y, z, 1, 0, 0, 0, 0);
                }
            }
            for (double x = minX; x <= maxX; x += 1.0) {
                for (double y = minY; y <= maxY; y += 1.0) {
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, y, minZ, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, y, maxZ, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    public void saveZones() {
        configHandler.saveZonesToConfig(zones);
    }

    public void exit() {
        saveZones();
        activeTasks.clear();
        zones.clear();
    }
}