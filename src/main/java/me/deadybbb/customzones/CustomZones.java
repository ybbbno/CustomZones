package me.deadybbb.customzones;

import me.deadybbb.customzones.listeners.DefaultZoneListener;
import me.deadybbb.customzones.listeners.VisibleZoneListener;
import me.deadybbb.customzones.listeners.ZoneListenerRegistry;
import me.deadybbb.customzones.prefixes.PrefixConfigHandler;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;

public final class CustomZones extends PluginProvider {
    private ZoneHandler handler;
    private PrefixHandler prefixHandler;

    @Override
    public void onEnable() {
        prefixHandler = new PrefixHandler(this, new PrefixConfigHandler(this));
        handler = new ZoneHandler(this, prefixHandler);
        ZoneListenerRegistry.initialize(this, prefixHandler);

        Bukkit.getPluginManager().registerEvents(new DefaultZoneListener(handler), this);
        ZoneListenerRegistry.registerListener(this, new VisibleZoneListener());

        new CustomZonesCommand(this, handler, prefixHandler).registerCommand();

        handler.startTimer(0L, 20L);
    }

    @Override
    public void onDisable() {
        if (handler != null) {
            handler.exit();
        }
    }
}
