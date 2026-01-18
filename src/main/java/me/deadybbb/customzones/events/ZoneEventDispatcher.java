package me.deadybbb.customzones.events;

import me.deadybbb.customzones.zone.Zone;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
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
        ZoneSpawnEvent event = new ZoneSpawnEvent(zone, uuid, spawnReason);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEnterEvent onZoneEnter(Zone zone, UUID uuid) {
        ZoneEnterEvent event = new ZoneEnterEvent(zone, uuid);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneExitEvent onZoneExit(Zone zone, UUID uuid, int ticksSpent) {
        ZoneExitEvent event = new ZoneExitEvent(zone, uuid, ticksSpent);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneStayEvent onZoneStay(Zone zone, UUID uuid, int currentTicks) {
        ZoneStayEvent event = new ZoneStayEvent(zone, uuid, currentTicks);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneTickEvent onZoneTick(Zone zone, List<UUID> uuids) {
        ZoneTickEvent event = new ZoneTickEvent(zone, uuids);
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
