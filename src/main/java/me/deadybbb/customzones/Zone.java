package me.deadybbb.myrosynthesis.customzone;

import org.bukkit.Location;

import java.util.List;

public class Zone {
    public String name;
    public Location min;
    public Location max;
    public List<String> effects;
    public boolean displayEnabled;

    public Zone(String name, Location min, Location max, List<String> effects, boolean displayEnabled) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.effects = effects;
        this.displayEnabled = displayEnabled;
    }
}
