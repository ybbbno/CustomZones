package me.deadybbb.customzones;

import me.deadybbb.customzones.events.*;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneHandler {
    private final ZoneConfigHandler configHandler;
    private final PluginProvider plugin;
    private final PrefixHandler handler;
    private final ZoneEventDispatcher dispatcher;

    public List<Zone> zones;
    public final Map<UUID, Location> pos1 = new HashMap<>();
    public final Map<UUID, Location> pos2 = new HashMap<>();

    private final Map<UUID, Map<String, BukkitRunnable>> activeTasks = new HashMap<>();
    private final Map<String, Set<UUID>> zoneEntities = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> entityTicks = new HashMap<>();

    public ZoneHandler(PluginProvider plugin, PrefixHandler handler) {
        configHandler = new ZoneConfigHandler(plugin);
        zones = configHandler.loadZonesFromConfig();
        this.plugin = plugin;
        this.handler = handler;
        this.dispatcher = new ZoneEventDispatcher(plugin, handler);
    }

    public void triggerEnter(UUID uuid, Zone zone) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        if (entity != null) {
            if (!dispatcher.onZoneEnter(zone, uuid).isCancelled()) {
                startTask(uuid, zone);
            }
        }
    }

    public void triggerExit(UUID uuid, Zone zone) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        if (entity != null) {
            int ticksSpent = entityTicks.getOrDefault(uuid, new HashMap<>()).getOrDefault(zone.name, 0);
            if (!dispatcher.onZoneExit(zone, uuid, ticksSpent).isCancelled()) {
                cancelTask(uuid, zone);
            }
        }
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
                            triggerEnter(uuid, zone);
                        }
                    }

                    // Detect exits
                    for (UUID uuid : new HashSet<>(previousUUIDs)) {
                        if (!currentUUIDs.contains(uuid)) {
                            triggerExit(uuid, zone);
                        }
                    }

                    zoneEntities.put(zone.name, currentUUIDs);

                    dispatcher.onZoneTick(zone, currentEntities);
                }
            }
        }.runTaskTimer(plugin, delay, period);
    }

    private void startTask(UUID uuid, Zone zone) {
        Map<String, BukkitRunnable> entityTasks = activeTasks.computeIfAbsent(uuid, k -> new HashMap<>());
        if (!entityTasks.containsKey(zone.name)) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    int ticks = entityTicks.computeIfAbsent(uuid, k -> new HashMap<>()).compute(zone.name, (k, v) -> v == null ? 20 : v + 20);
                    dispatcher.onZoneStay(zone, uuid, ticks);
                    // plugin.logger.info(entity.getName() + ", " + ticks); remove player on exit
                }
            };
            task.runTaskTimer(plugin, 0L, 20L);
            entityTasks.put(zone.name, task);
        }
    }

    private void cancelTask(UUID uuid, Zone zone) {
        Map<String, BukkitRunnable> entityTasks = activeTasks.get(uuid);
        if (entityTasks != null) {
            BukkitRunnable task = entityTasks.remove(zone.name);
            if (task != null) {
                task.cancel();
            }
            if (entityTasks.isEmpty()) {
                activeTasks.remove(uuid);
            }
        }
        Map<String, Integer> ticks = entityTicks.get(uuid);
        if (ticks != null) {
            ticks.remove(zone.name);
            if (ticks.isEmpty()) {
                entityTicks.remove(uuid);
            }
        }
    }

    public ZoneEventDispatcher getDispatcher() {
        return dispatcher;
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

    public List<String> getAllZonesNames(String zoneName) {
        return zones.stream()
                .map(zone -> zone.name)
                .filter(name -> zoneName == null || name.toLowerCase().startsWith(zoneName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Zone getZoneByName(String name) {
        return zones.stream().filter(z -> z.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Zone> getZonesAtLocation(Location location) {
        return zones.stream()
                .filter(zone -> zone.min.getWorld().equals(location.getWorld()) &&
                        zone.getBoundingBox().contains(location.toVector()))
                .collect(Collectors.toList());
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