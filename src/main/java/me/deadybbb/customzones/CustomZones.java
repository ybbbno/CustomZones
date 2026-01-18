package me.deadybbb.customzones;

import me.deadybbb.customzones.listeners.DefaultZoneListener;
import me.deadybbb.customzones.listeners.VisibleZoneListener;
import me.deadybbb.customzones.listeners.ZoneListenerRegistry;
import me.deadybbb.customzones.prefixes.PrefixConfigHandler;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.customzones.zone.ZoneManager;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;

public final class CustomZones extends PluginProvider {
    private ZoneManager manager;
    private PrefixHandler prefixHandler;

    @Override
    public void onEnable() {
        prefixHandler = new PrefixHandler(this, new PrefixConfigHandler(this));
        manager = new ZoneManager(this, prefixHandler);
        ZoneListenerRegistry.initialize(this, prefixHandler);

        Bukkit.getPluginManager().registerEvents(new DefaultZoneListener(manager), this);
        ZoneListenerRegistry.registerListener(this, new VisibleZoneListener());

        new CustomZonesCommand(this, manager, prefixHandler).registerCommand();

        manager.init();
    }

    @Override
    public void onDisable() {
        manager.deinit();
    }

    public ZoneManager manager() { return manager; }
}
