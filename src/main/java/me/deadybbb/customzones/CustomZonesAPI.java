package me.deadybbb.customzones;

import me.deadybbb.customzones.zone.ZoneManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CustomZonesAPI {
    private final CustomZones plugin;

    private CustomZonesAPI(CustomZones plugin) { this.plugin = plugin; }

    public ZoneManager manager() { return plugin.manager(); }

    public static CustomZonesAPI api() {
        Plugin p = Bukkit.getPluginManager().getPlugin("CustomZones");
        if (p instanceof CustomZones) {
            return new CustomZonesAPI((CustomZones) p);
        }
        return null;
    }
}
