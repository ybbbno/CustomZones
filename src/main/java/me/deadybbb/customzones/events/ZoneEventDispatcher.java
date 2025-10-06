package me.deadybbb.customzones.events;

import me.deadybbb.customzones.Zone;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class ZoneEventDispatcher {
    private final PrefixHandler prefixHandler;
    private final PluginProvider plugin;

    public ZoneEventDispatcher(PluginProvider plugin, PrefixHandler prefixHandler) {
        this.prefixHandler = prefixHandler;
        this.plugin = plugin;
    }

    public ZoneSpawnEvent onZoneSpawn(Zone zone, UUID uuid, CreatureSpawnEvent.SpawnReason spawnReason) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        ZoneSpawnEvent event = new ZoneSpawnEvent(zone, entity, spawnReason);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneSpawnEvent onZoneSpawn(Zone zone, LivingEntity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
        ZoneSpawnEvent event = new ZoneSpawnEvent(zone, entity, spawnReason);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneSpawnEvent onZoneSpawn(Zone zone, Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
        ZoneSpawnEvent event = new ZoneSpawnEvent(zone, (LivingEntity) entity, spawnReason);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEnterEvent onZoneEnter(Zone zone, UUID uuid) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        ZoneEnterEvent event = new ZoneEnterEvent(zone, entity);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEnterEvent onZoneEnter(Zone zone, LivingEntity entity) {
        ZoneEnterEvent event = new ZoneEnterEvent(zone, entity);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEnterEvent onZoneEnter(Zone zone, Entity entity) {
        ZoneEnterEvent event = new ZoneEnterEvent(zone, (LivingEntity) entity);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneExitEvent onZoneExit(Zone zone, UUID uuid, int ticksSpent) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        ZoneExitEvent event = new ZoneExitEvent(zone, entity, ticksSpent);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneExitEvent onZoneExit(Zone zone, LivingEntity entity, int ticksSpent) {
        ZoneExitEvent event = new ZoneExitEvent(zone, entity, ticksSpent);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneExitEvent onZoneExit(Zone zone, Entity entity, int ticksSpent) {
        ZoneExitEvent event = new ZoneExitEvent(zone, (LivingEntity) entity, ticksSpent);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneStayEvent onZoneStay(Zone zone, UUID uuid, int currentTicks) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        ZoneStayEvent event = new ZoneStayEvent(zone, entity, currentTicks);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneStayEvent onZoneStay(Zone zone, LivingEntity entity, int currentTicks) {
        ZoneStayEvent event = new ZoneStayEvent(zone, entity, currentTicks);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneStayEvent onZoneStay(Zone zone, Entity entity, int currentTicks) {
        ZoneStayEvent event = new ZoneStayEvent(zone, (LivingEntity) entity, currentTicks);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneTickEvent onZoneTick(Zone zone, List<LivingEntity> entities) {
        ZoneTickEvent event = new ZoneTickEvent(zone, entities);
        triggerEvent(event, zone);
        return event;
    }

    public void triggerEvent(Event event, Zone zone) {
        List<String> zonePrefixes = zone.prefixes.stream().map(String::toLowerCase).toList();

        for (String zonePrefix : zonePrefixes) {
            Listener listener = prefixHandler.getListenerByPrefix(zonePrefix);
            if (listener == null) continue;

            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class) &&
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    try {
                        method.setAccessible(true);
                        method.invoke(listener, event);
                    } catch (Exception ex) {
                        plugin.logger.severe("Error invoking event handler for prefix '" + zonePrefix + "': " + ex.getMessage());
                    }
                    break;
                }
            }
        }
    }
}
