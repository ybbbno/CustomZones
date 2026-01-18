package me.deadybbb.customzones.zone;

import me.deadybbb.customzones.events.*;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.BasicManagerHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneManager extends BasicManagerHandler {
    private final ZoneConfigManager configHandler;
    private final ZoneEventDispatcher dispatcher;

    private final List<Zone> zones;

    private final Map<UUID, Map<String, BukkitRunnable>> activeTasks = new HashMap<>();
    private final Map<String, Set<UUID>> zoneEntities = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> entityTicks = new HashMap<>();

    public ZoneManager(PluginProvider plugin, PrefixHandler handler) {
        super(plugin);
        zones = new ArrayList<>();
        configHandler = new ZoneConfigManager(plugin);
        dispatcher = new ZoneEventDispatcher(plugin, handler);
    }

    @Override
    protected void onInit() {
        zones.clear();
        zones.addAll(configHandler.loadZonesFromConfig());
        startTimer(0L, 20L);
    }

    @Override
    protected void onDeinit() {
        configHandler.saveZonesToConfig(zones);
        exit();
    }

    public void triggerEnter(UUID uuid, Zone zone) {
        if (!dispatcher.onZoneEnter(zone, uuid).isCancelled()) {
            startTask(uuid, zone);
        }
    }

    public void triggerExit(UUID uuid, Zone zone) {
        int ticksSpent = entityTicks.getOrDefault(uuid, new HashMap<>()).getOrDefault(zone.name, 0);
        if (!dispatcher.onZoneExit(zone, uuid, ticksSpent).isCancelled()) {
            cancelTask(uuid, zone);
        }
    }

    private void startTimer(long delay, long period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Zone zone : zones) {
                    if (zone.min.getWorld() == null) continue;

                    // Finding current entities
                    List<Entity> currentEntities = zone.min.getWorld().getNearbyEntities(
                            zone.getBoundingBox(),
                            Objects::nonNull
                    ).stream().toList();

                    String zoneName = zone.name;
                    Set<UUID> previousUUIDs = zoneEntities.computeIfAbsent(zoneName, k -> new HashSet<>());
                    Set<UUID> currentUUIDs = currentEntities.stream().map(Entity::getUniqueId).collect(Collectors.toSet());

                    // Detect enters
                    for (UUID uuid : currentUUIDs) {
                        if (!previousUUIDs.contains(uuid)) {
                            triggerEnter(uuid, zone);
                        }
                    }

                    // Detect exits
                    for (UUID uuid : previousUUIDs) {
                        if (!currentUUIDs.contains(uuid)) {
                            triggerExit(uuid, zone);
                        }
                    }

                    zoneEntities.put(zone.name, currentUUIDs);

                    dispatcher.onZoneTick(zone, currentUUIDs.stream().toList());
                }
            }
        }.runTaskTimer(plugin, delay, period);
    }

    private void exit() {
        for (Map<String, BukkitRunnable> tasks : activeTasks.values()) {
            for (BukkitRunnable task : tasks.values()) {
                task.cancel();
            }
        }
        activeTasks.clear();
        entityTicks.clear();
        zoneEntities.clear();
    }

    private void startTask(UUID uuid, Zone zone) {
        Map<String, BukkitRunnable> entityTasks = activeTasks.computeIfAbsent(uuid, k -> new HashMap<>());
        if (!entityTasks.containsKey(zone.name)) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    int ticks = entityTicks.computeIfAbsent(uuid, k -> new HashMap<>()).compute(zone.name, (k, v) -> v == null ? 20 : v + 20);
                    dispatcher.onZoneStay(zone, uuid, ticks);
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

    public boolean addZone(String name, Location p1, Location p2, List<String> prefixes) {
        return addZone(new Zone(name, p1, p2, prefixes));
    }

    public boolean addZone(Zone zone) {
        removeZone(zone);
        for (Entity entity : zone.min.getWorld().getNearbyEntities(zone.getBoundingBox(), Objects::nonNull).stream().toList()) {
            triggerEnter(entity.getUniqueId(), zone);
        }
        return zones.add(zone);
    }

    public boolean removeZone(String name) {
        return removeZone(getZoneByName(name));
    }

    public boolean removeZone(Zone zone) {
        if (getZoneByName(zone.name) == null) return false;
        for (UUID uuid : zoneEntities.get(zone.name)) {
            triggerExit(uuid, zone);
        }
        return zones.remove(zone);
    }
}