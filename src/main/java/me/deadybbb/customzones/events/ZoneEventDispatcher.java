package me.deadybbb.customzones.events;

import me.deadybbb.customzones.events.entity.*;
import me.deadybbb.customzones.events.entity.hanging.ZoneHangingBreakEvent;
import me.deadybbb.customzones.events.entity.hanging.ZoneHangingPlaceEvent;
import me.deadybbb.customzones.events.zone.*;
import me.deadybbb.customzones.events.zone.command.*;
import me.deadybbb.customzones.zone.Zone;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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

    public ZoneCreateEvent onZoneCreate(Zone zone) {
        ZoneCreateEvent event = new ZoneCreateEvent(zone);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneRemoveEvent onZoneRemove(Zone zone) {
        ZoneRemoveEvent event = new ZoneRemoveEvent(zone);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneCommandChangeEvent onZoneCommandChange(Zone zone) {
        ZoneCommandChangeEvent event = new ZoneCommandChangeEvent(zone);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneCommandAddPrefixEvent onZoneCommandAddPrefix(Zone zone) {
        ZoneCommandAddPrefixEvent event = new ZoneCommandAddPrefixEvent(zone);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneCommandRemovePrefixEvent onZoneCommandRemovePrefix(Zone zone) {
        ZoneCommandRemovePrefixEvent event = new ZoneCommandRemovePrefixEvent(zone);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneCreatureSpawnEvent onZoneCreatureSpawn(Zone zone, UUID uuid, CreatureSpawnEvent.SpawnReason spawnReason) {
        ZoneCreatureSpawnEvent event = new ZoneCreatureSpawnEvent(zone, uuid, spawnReason);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEntityEnterEvent onZoneEntityEnter(Zone zone, UUID uuid) {
        ZoneEntityEnterEvent event = new ZoneEntityEnterEvent(zone, uuid);
//        plugin.logger.info(event.getEventName()+" "+zone.name+" "+ Objects.requireNonNull(Bukkit.getEntity(event.getEntityUUID())).getName());
        triggerEvent(event, zone);
        return event;
    }

    public ZoneEntityExitEvent onZoneEntityExit(Zone zone, UUID uuid, int ticksSpent) {
        ZoneEntityExitEvent event = new ZoneEntityExitEvent(zone, uuid, ticksSpent);
        triggerEvent(event, zone);
//        plugin.logger.info(event.getEventName()+" "+zone.name+" "+ Objects.requireNonNull(Bukkit.getEntity(event.getEntityUUID())).getName());
        return event;
    }

    public ZoneEntityStayEvent onZoneEntityStay(Zone zone, UUID uuid, int currentTicks) {
        ZoneEntityStayEvent event = new ZoneEntityStayEvent(zone, uuid, currentTicks);
        triggerEvent(event, zone);
//        plugin.logger.info(event.getEventName()+" "+zone.name+" "+ Objects.requireNonNull(Bukkit.getEntity(event.getEntityUUID())).getName());
        return event;
    }

    public ZoneTickEvent onZoneTick(Zone zone, List<UUID> uuids) {
        ZoneTickEvent event = new ZoneTickEvent(zone, uuids);
        triggerEvent(event, zone);
        return event;
    }

    public ZonePlayerPlaceBlockEvent onZonePlayerPlaceBlock(Zone zone, UUID uuid, Block block) {
        ZonePlayerPlaceBlockEvent event = new ZonePlayerPlaceBlockEvent(zone, uuid, block);
        triggerEvent(event, zone);
        return event;
    }

    public ZonePlayerBreakBlockEvent onZonePlayerBreakBlock(Zone zone, UUID uuid, Block block) {
        ZonePlayerBreakBlockEvent event = new ZonePlayerBreakBlockEvent(zone, uuid, block);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneHangingPlaceEvent onZoneHangingPlace(Zone zone, Hanging hanging, Player player, Block block, BlockFace blockFace, EquipmentSlot hand, ItemStack itemStack) {
        ZoneHangingPlaceEvent event = new ZoneHangingPlaceEvent(zone, hanging, player, block, blockFace, hand, itemStack);
        triggerEvent(event, zone);
        return event;
    }

    public ZoneHangingBreakEvent onZoneHangingBreak(Zone zone, Hanging hanging, HangingBreakEvent.RemoveCause cause) {
        ZoneHangingBreakEvent event = new ZoneHangingBreakEvent(zone, hanging, cause);
        triggerEvent(event, zone);
        return event;
    }

    public void triggerEvent(ZoneEvent event, Zone zone) {
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
