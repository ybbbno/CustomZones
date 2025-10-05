package me.deadybbb.customzones;

import me.deadybbb.ybmj.BasicConfigHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ZoneConfigHandler extends BasicConfigHandler {
    private final PluginProvider plugin;

    public ZoneConfigHandler(PluginProvider plugin) {
        super(plugin, "zones_config.yml");
        this.plugin = plugin;
    }

    public List<Zone> loadZonesFromConfig() {
        reloadConfig();
        List<Zone> zones = new ArrayList<>();
        ConfigurationSection zonesSection = config.getConfigurationSection("zones");
        if (zonesSection == null) return zones;

        for (String zoneID : zonesSection.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("zones." + zoneID);
            if (section == null) continue;

            String worldName = section.getString(".world");
            if (worldName == null) {
                plugin.logger.warning("Invalid world for zone " + zoneID);
                continue;
            }
            if (Bukkit.getWorld(worldName) == null) {
                plugin.logger.warning("World " + worldName + " not found for zone " + zoneID);
            }

            Location min = new Location(
                    Bukkit.getWorld(worldName),
                    section.getDouble(".min.x"),
                    section.getDouble(".min.y"),
                    section.getDouble(".min.z")
            );

            Location max = new Location(
                    Bukkit.getWorld(worldName),
                    section.getDouble(".max.x"),
                    section.getDouble(".max.y"),
                    section.getDouble(".max.z")
            );

            List<String> prefixes = section.getStringList(".prefixes");
            zones.add(new Zone(zoneID, min, max, prefixes));
        }
        plugin.logger.info("Loaded " + zones.size() + " zones.");
        return zones;
    }

    public boolean saveZonesToConfig(List<Zone> zones) {
        config.set("zones", null);

        for (Zone zone : zones) {
            String path = "zones." + zone.name;
            config.set(path + ".world", zone.min.getWorld().getName());
            config.set(path + ".min.x", zone.min.getX());
            config.set(path + ".min.y", zone.min.getY());
            config.set(path + ".min.z", zone.min.getZ());
            config.set(path + ".max.x", zone.max.getX());
            config.set(path + ".max.y", zone.max.getY());
            config.set(path + ".max.z", zone.max.getZ());
            config.set(path + ".prefixes", zone.prefixes);
        }

        return saveConfig();
    }
}