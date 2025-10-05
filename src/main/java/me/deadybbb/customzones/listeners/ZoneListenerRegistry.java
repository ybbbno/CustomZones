package me.deadybbb.customzones.listeners;

import me.deadybbb.customzones.CustomZones;
import me.deadybbb.customzones.prefixes.CustomZonePrefix;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.BasicLoggerHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ZoneListenerRegistry {
    private static PrefixHandler handler;
    private static BasicLoggerHandler logger;

    private ZoneListenerRegistry() {}

    public static void initialize(CustomZones plugin, PrefixHandler handler) {
        ZoneListenerRegistry.logger = plugin.logger;
        ZoneListenerRegistry.handler = handler;
    }

    public static boolean registerListener(JavaPlugin plugin, Listener listener) {
        if (plugin == null || listener == null) {
            logger.warning("Invalid registration attempt: Plugin or listener is null.");
            return false;
        }

        Class<?> clazz = listener.getClass();
        CustomZonePrefix prefix = clazz.getAnnotation(CustomZonePrefix.class);
        if (prefix == null || prefix.value() == null || prefix.value().trim().isEmpty()) {
            logger.warning("Listener " + clazz.getName() + " does not have a valid @CustomZonePrefix annotation.");
            return false;
        }

        String prefixS = prefix.value().toLowerCase();

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        return handler.registerPrefix(prefixS, listener);
    }

    public static boolean unregisterListener(String prefix) {
        return handler.unregisterPrefix(prefix);
    }

    public static List<String> getPrefixes() {
        return handler.getPrefixes();
    }
}
