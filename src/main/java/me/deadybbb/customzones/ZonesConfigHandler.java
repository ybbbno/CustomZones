package me.deadybbb.myrosynthesis.customzone;

import me.deadybbb.myrosynthesis.basic.BasicConfigHandler;
import me.deadybbb.myrosynthesis.custombooks.Book;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZonesConfigHandler extends BasicConfigHandler {
    public ZonesConfigHandler(JavaPlugin plugin) {
        super(plugin, "zones_config.yml");
    }

    public List<Zone> loadZonesFromConfig() {
        reloadConfig();
        List<Zone> zones = new ArrayList<>();
        if (config.getConfigurationSection("zones") == null) return zones;

        for (String zoneName : config.getConfigurationSection("zones").getKeys(false)) {
            String path = "zones." + zoneName;
            String worldName = config.getString(path + ".world");
            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                plugin.getLogger().warning("Invalid world for zone " + zoneName);
                continue;
            }
            Location min = new Location(
                    Bukkit.getWorld(worldName),
                    config.getDouble(path + ".min.x"),
                    config.getDouble(path + ".min.y"),
                    config.getDouble(path + ".min.z")
            );
            Location max = new Location(
                    Bukkit.getWorld(worldName),
                    config.getDouble(path + ".max.x"),
                    config.getDouble(path + ".max.y"),
                    config.getDouble(path + ".max.z")
            );

            List<String> effects = config.getStringList(path + ".effects");
            boolean display = config.getBoolean(path + ".display", false);
            zones.add(new Zone(zoneName, min, max, effects, display));
        }
        plugin.getLogger().info("Loaded " + zones.size() + " zones.");
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
            config.set(path + ".effects", zone.effects);
            config.set(path + ".display", zone.displayEnabled);
        }

        return saveConfig();
    }
}