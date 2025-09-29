package me.deadybbb.customzones;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    public String name;
    public Location min;
    public Location max;
    public boolean displayEnabled;

    public Zone(String name, Location min, Location max, boolean displayEnabled) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.displayEnabled = displayEnabled;

    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.of(min, max);
    }
}
