package me.deadybbb.customzones;

import me.deadybbb.customzones.events.ZoneEnterEvent;
import me.deadybbb.customzones.events.ZoneExitEvent;
import me.deadybbb.customzones.events.ZoneStayEvent;
import me.deadybbb.customzones.events.ZoneTickEvent;
import me.deadybbb.customzones.prefixes.CustomZonePrefix;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ZoneHandler {
    private final ZoneConfigHandler configHandler;
    private final PluginProvider plugin;
    private final PrefixHandler handler;

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
                                triggerEnterEvent(uuid, zone);
                            }
                        }
                    }

                    // Detect exits
                    for (UUID uuid : new HashSet<>(previousUUIDs)) {
                        if (!currentUUIDs.contains(uuid)) {
                            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                            if (entity != null) {
                                triggerExitEvent(uuid, zone);
                            }
                        }
                    }

                    zoneEntities.put(zone.name, currentUUIDs);

                    ZoneTickEvent tickEvent = new ZoneTickEvent(zone, currentEntities);
                    callEventWithPrefixFilter(tickEvent, zone);
                }
            }
        }.runTaskTimer(plugin, delay, period);
    }

    public void triggerExitEvent(UUID uuid, Zone zone) {
        int ticksSpent = entityTicks.getOrDefault(uuid, new HashMap<>()).getOrDefault(zone.name, 0);
        ZoneExitEvent exitEvent = new ZoneExitEvent(zone, (LivingEntity) Bukkit.getEntity(uuid), ticksSpent);
        callEventWithPrefixFilter(exitEvent, zone);
        cancelTask(uuid, zone);
    }

    public void triggerEnterEvent(UUID uuid, Zone zone) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        ZoneEnterEvent enterEvent = new ZoneEnterEvent(zone, entity);
        callEventWithPrefixFilter(enterEvent, zone);
        if (!enterEvent.isCancelled()) {
            startTask(entity, zone); // per-entity
        }
    }

    private void startTask(LivingEntity entity, Zone zone) {
        UUID uuid = entity.getUniqueId();
        Map<String, BukkitRunnable> entityTasks = activeTasks.computeIfAbsent(uuid, k -> new HashMap<>());
        if (!entityTasks.containsKey(zone.name)) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    int ticks = entityTicks.computeIfAbsent(uuid, k -> new HashMap<>()).compute(zone.name, (k, v) -> v == null ? 20 : v + 20);
                    ZoneStayEvent stayEvent = new ZoneStayEvent(zone, entity, ticks);
                    callEventWithPrefixFilter(stayEvent, zone);
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

    private void callEventWithPrefixFilter(Event event, Zone zone) {
        List<String> zonePrefixes = zone.prefixes.stream().map(String::toLowerCase).toList();

        for (String zonePrefix : zonePrefixes) {
            Listener listener = handler.getListenerByPrefix(zonePrefix);
            if (listener == null) {
                continue;
            }

            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class) &&
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    try {
                        method.setAccessible(true);
                        method.invoke(listener, event);
                        plugin.logger.info("Invoked " + method.getName() + " on listener for prefix '" +
                                zonePrefix + "' (event: " + event.getEventName() + ")");
                    } catch (Exception ex) {
                        plugin.logger.severe("Error invoking event handler for prefix '" + zonePrefix + "': " + ex.getMessage());
                    }
                    break;
                }
            }
        }
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