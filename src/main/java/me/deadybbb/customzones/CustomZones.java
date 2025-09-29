package me.deadybbb.customzones;

import co.aikar.commands.CommandManager;
import co.aikar.commands.PaperCommandManager;
import me.deadybbb.ybmj.PluginProvider;
import org.bukkit.Bukkit;

public final class CustomZones extends PluginProvider {
    private ZonesHandler handler;
    public CommandManager commandManager;

    @Override
    public void onEnable() {
        handler = new ZonesHandler(this);

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ZonesCommand(this, handler));

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
