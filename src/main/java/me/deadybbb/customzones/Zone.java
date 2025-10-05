package me.deadybbb.customzones;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    public String name;
    public Location min;
    public Location max;
    public List<String> prefixes;

    public Zone(String name, Location min, Location max, List<String> prefixes) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.prefixes = prefixes;
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.of(min, max);
    }

    public boolean hasPrefix(String prefix) {
        return prefixes.contains(prefix.toLowerCase());
    }

    public void addPrefix(String prefix) {
        if (!prefixes.contains(prefix.toLowerCase())) {
            prefixes.add(prefix.toLowerCase());
        }
    }

    public void removePrefix(String prefix) {
        prefixes.remove(prefix.toLowerCase());
    }
}
