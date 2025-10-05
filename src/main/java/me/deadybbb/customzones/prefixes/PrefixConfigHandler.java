package me.deadybbb.customzones.prefixes;

import me.deadybbb.ybmj.BasicConfigHandler;
import me.deadybbb.ybmj.PluginProvider;

import java.util.List;

public class PrefixConfigHandler extends BasicConfigHandler {
    private final PluginProvider plugin;

    public PrefixConfigHandler(PluginProvider plugin) {
        super(plugin, "prefixes.yml");
        this.plugin = plugin;
    }

    public List<String> loadPrefixes() {
        reloadConfig();
        List<String> prefixes = config.getStringList("prefixes");
        plugin.logger.info("Loaded " + prefixes.size() + " prefixes.");
        return prefixes;
    }

    public void savePrefixes(List<String> prefixes) {
        config.set("prefixes", prefixes);
        saveConfig();
    }
}
