package me.deadybbb.customzones;

import me.deadybbb.customzones.events.ZoneEnterEvent;
import me.deadybbb.customzones.events.ZoneExitEvent;
import me.deadybbb.customzones.events.ZoneStayEvent;
import me.deadybbb.customzones.events.ZoneTickEvent;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ZonesHandler {
    private final ZonesConfigHandler configHandler;;
    private final PluginProvider plugin;

    public List<Zone> zones;
    public final Map<UUID, Location> pos1 = new HashMap<>();
    public final Map<UUID, Location> pos2 = new HashMap<>();

    private final Map<UUID, Map<String, BukkitRunnable>> activeTasks = new HashMap<>();
    private final Map<String, Set<UUID>> zoneEntities = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> entityTicks = new HashMap<>();

    public ZonesHandler(PluginProvider plugin) {
        configHandler = new ZonesConfigHandler(plugin);
        zones = configHandler.loadZonesFromConfig();
        this.plugin = plugin;
    }

    public boolean reloadZonesFromConfig() {
        try {
            zones = configHandler.loadZonesFromConfig();
            return true;
        } catch (Exception e) {
            plugin.logger.severe("Failed to load zones config:" + e);
            return false;
        }
    }

    public void startTask(LivingEntity entity, Zone zone, long delay, long period) {
        if (entity == null || zone == null || delay < 0 || period <= 0) return;
        if (!isEntityInZone(entity, zone)) return;

        UUID entityId = entity.getUniqueId();
        String zoneName = zone.name;

        activeTasks.computeIfAbsent(entityId, k -> new HashMap<>());

        if (activeTasks.get(entityId).containsKey(zoneName)) return;

        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!entity.isValid() || !isEntityInZone(entity, zone)) {
                    cancel();
                    return;
                }

                ZoneStayEvent stayEvent = new ZoneStayEvent(zone, entity, ticks);
                Bukkit.getPluginManager().callEvent(stayEvent);

                ticks += (int) period;

                entityTicks.computeIfAbsent(entityId, k -> new HashMap<>()).put(zone.name, ticks);
            }
        };

        task.runTaskTimer(plugin, delay, period);
        activeTasks.get(entityId).put(zoneName, task);
    }

    public void startTimer(long delay, long period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Zone zone : zones) {
                    if (zone.min.getWorld() == null) continue;

                    // Finding current entities
                    List<LivingEntity> currentEntities = zone.min.getWorld().getNearbyEntities(
                            zone.getBoundingBox(),
                            e -> e instanceof LivingEntity
                    ).stream().map(e -> (LivingEntity) e).toList();

                    String zoneName = zone.name;
                    Set<UUID> previousUUIDs = zoneEntities.computeIfAbsent(zoneName, k -> new HashSet<>());
                    Set<UUID> currentUUIDs = currentEntities.stream().map(LivingEntity::getUniqueId).collect(Collectors.toSet());

                    // Detect enters
                    for (UUID uuid : currentUUIDs) {
                        if (!previousUUIDs.contains(uuid)) {
                            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                            if (entity != null) {
                                ZoneEnterEvent enterEvent = new ZoneEnterEvent(zone, entity);
                                Bukkit.getPluginManager().callEvent(enterEvent);
                                if (!enterEvent.isCancelled()) {
                                    startTask(entity, zone, 0L, period); // per-entity
                                }
                            }
                        }
                    }

                    // Detect exits
                    for (UUID uuid : new HashSet<>(previousUUIDs)) {
                        if (!currentUUIDs.contains(uuid)) {
                            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                            if (entity != null) {
                                int ticksSpent = entityTicks.getOrDefault(uuid, new HashMap<>()).getOrDefault(zoneName, 0);
                                ZoneExitEvent exitEvent = new ZoneExitEvent(zone, entity, ticksSpent);
                                Bukkit.getPluginManager().callEvent(exitEvent);
                                if (activeTasks.containsKey(uuid) && activeTasks.get(uuid).containsKey(zoneName)) {
                                    activeTasks.get(uuid).get(zoneName).cancel();
                                    activeTasks.get(uuid).remove(zoneName);
                                    if (activeTasks.get(uuid).isEmpty()) {
                                        activeTasks.remove(uuid);
                                    }
                                }
                                if (entityTicks.containsKey(uuid)) {
                                    entityTicks.get(uuid).remove(zoneName);
                                    if (entityTicks.get(uuid).isEmpty()) {
                                        entityTicks.remove(uuid);
                                    }
                                }

                            }
                        }
                    }

                    previousUUIDs.clear();
                    previousUUIDs.addAll(currentUUIDs);

                    ZoneTickEvent tickEvent = new ZoneTickEvent(zone, currentEntities);
                    Bukkit.getPluginManager().callEvent(tickEvent);
                }
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

    private boolean isLocationInZone(Location loc, Zone zone) {
        return loc.getWorld().equals(zone.min.getWorld()) &&
                loc.getX() >= Math.min(zone.min.getX(), zone.max.getX()) &&
                loc.getX() <= Math.max(zone.min.getX(), zone.max.getX()) &&
                loc.getY() >= Math.min(zone.min.getY(), zone.max.getY()) &&
                loc.getY() <= Math.max(zone.min.getY(), zone.max.getY()) &&
                loc.getZ() >= Math.min(zone.min.getZ(), zone.max.getZ()) &&
                loc.getZ() <= Math.max(zone.min.getZ(), zone.max.getZ());
    }

    public List<String> getAllZonesNames(String zoneName) {
        if (Objects.equals(zoneName, "")) {
            return zones.stream()
                    .map(zone -> zone.name)
                    .toList();
        }
        return zones.stream()
                .map(zone -> zone.name)
                .filter(name -> name.toLowerCase().startsWith(zoneName.toLowerCase()))
                .toList();
    }

    public Zone getZoneByName(String name) {
        return zones.stream().filter(z -> z.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Zone> getZonesAtLocation(Location location) {
        return zones.stream().filter(z -> isLocationInZone(location, z)).collect(Collectors.toList());
    }

    public void saveZones() {
        configHandler.saveZonesToConfig(zones);
    }

    public void exit() {
        saveZones();
        for (Map<String, BukkitRunnable> tasks : activeTasks.values()) {
            for (BukkitRunnable task : tasks.values()) {
                task.cancel();
            }
        }
        activeTasks.clear();
        entityTicks.clear();
        zoneEntities.clear();
        zones.clear();
    }
}