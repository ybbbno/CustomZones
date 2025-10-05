package me.deadybbb.customzones.prefixes;

import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.event.Listener;

import java.util.*;

public class PrefixHandler {
    private final PluginProvider plugin;
    private final PrefixConfigHandler config;
    private final Map<String, Listener> prefixToListener = new HashMap<>();
    private final List<String> prefixes = new ArrayList<>();

    public PrefixHandler(PluginProvider plugin, PrefixConfigHandler config) {
        this.plugin = plugin;
        this.config = config;
        this.prefixes.addAll(config.loadPrefixes());
    }

    public boolean registerPrefix(String prefix, Listener listenerClass) {
        if (prefix == null || prefix.trim().isEmpty()) {
            plugin.logger.warning("Invalid prefix registration attempt: Prefix is null/empty.");
            return false;
        }

        String lowerPrefix = prefix.toLowerCase();

        if (!prefixes.contains(lowerPrefix)) {
            prefixes.add(lowerPrefix);
            config.savePrefixes(prefixes);
        }

        prefixToListener.put(lowerPrefix, listenerClass);

        return true;
    }

    public boolean unregisterPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            plugin.logger.warning("Invalid prefix unregistration attempt: Prefix is null/empty.");
            return false;
        }

        String lowerPrefix = prefix.toLowerCase();
        if (!prefixes.contains(lowerPrefix)) {
            plugin.logger.warning("Prefix " + lowerPrefix + " is not registered.");
            return false;
        }

        prefixes.remove(lowerPrefix);
        prefixToListener.remove(lowerPrefix);

        config.savePrefixes(prefixes);
        return true;
    }

    public List<String> getPrefixes() {
        return List.copyOf(prefixes);
    }

    public Listener getListenerByPrefix(String prefix) {
        return prefixToListener.get(prefix.toLowerCase());
    }

    public Collection<Listener> getAllListeners() {
        return Collections.unmodifiableCollection(prefixToListener.values());
    }
}
