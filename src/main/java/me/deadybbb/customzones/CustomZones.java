package me.deadybbb.customzones;

import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;

public final class CustomZones extends PluginProvider {
    private ZonesHandler handler;

    @Override
    public void onEnable() {
        handler = new ZonesHandler(this);

        new CustomZonesCommand(this, handler).registerCommand();

        handler.startTimer(0L, 20L);

        Bukkit.getPluginManager().registerEvents(new DefaultZoneListener(this), this);
    }

    @Override
    public void onDisable() {
        if (handler != null) {
            handler.exit();
        }
    }
}
