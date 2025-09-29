package me.deadybbb.customzones;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import me.deadybbb.ybmj.LegacyTextHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CustomZonesCommand {
    private final CustomZones plugin;
    private final ZonesHandler handler;

    public CustomZonesCommand(CustomZones plugin, ZonesHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    public void registerCommand() {
        // Main command
        CommandAPICommand zoneCommand = new CommandAPICommand("zone")
                .withPermission("customzones.zone")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Эта команда только для игроков!");
                        return;
                    }
                    LegacyTextHandler.sendFormattedMessage((Player) sender, "<red>Использование: /zone <create|pos1|pos2|toggle|remove|change|reload> [name]");
                });

        // Subcommand: pos1
        CommandAPICommand pos1 = new CommandAPICommand("pos1")
                .withShortDescription("Устанавливает первую точку зоны")
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    handler.pos1.put(player.getUniqueId(), player.getLocation());
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Первая точка установлена: " + LegacyTextHandler.formatLocation(player.getLocation()));
                });

        // Subcommand: pos2
        CommandAPICommand pos2 = new CommandAPICommand("pos2")
                .withShortDescription("Устанавливает вторую точку зоны")
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    handler.pos2.put(player.getUniqueId(), player.getLocation());
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Вторая точка установлена: " + LegacyTextHandler.formatLocation(player.getLocation()));
                });

        // Subcommand: create
        CommandAPICommand create = new CommandAPICommand("create")
                .withShortDescription("Создает новую зону")
                .withArguments(new StringArgument("zoneName"))
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    if (handler.getZoneByName(zoneName) != null) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона с именем " + zoneName + " уже существует!");
                        return;
                    }
                    Location p1 = handler.pos1.get(player.getUniqueId());
                    Location p2 = handler.pos2.get(player.getUniqueId());
                    if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                        return;
                    }
                    handler.zones.add(new Zone(zoneName, p1, p2, false));
                    handler.saveZones();
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Зона " + zoneName + " создана!");
                });

        // Subcommand: toggle
        CommandAPICommand toggle = new CommandAPICommand("toggle")
                .withShortDescription("Включает или выключает отображение зоны")
                .withArguments(new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                        handler.getAllZonesNames("").toArray(new String[0]))))
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    Zone toggleZone = handler.getZoneByName(zoneName);
                    if (toggleZone == null) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
                        return;
                    }
                    toggleZone.displayEnabled = !toggleZone.displayEnabled;
                    handler.saveZones();
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Отображение зоны " + zoneName + " " + (toggleZone.displayEnabled ? "включено" : "выключено"));
                });

        // Subcommand: remove
        CommandAPICommand remove = new CommandAPICommand("remove")
                .withShortDescription("Удаляет зону")
                .withArguments(new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                        handler.getAllZonesNames("").toArray(new String[0]))))
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    Zone removeZone = handler.getZoneByName(zoneName);
                    if (removeZone != null) {
                        handler.zones.remove(removeZone);
                        handler.saveZones();
                        LegacyTextHandler.sendFormattedMessage(player, "<green>Зона " + zoneName + " удалена!");
                    } else {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
                    }
                });

        // Subcommand: change
        CommandAPICommand change = new CommandAPICommand("change")
                .withShortDescription("Изменяет границы зоны")
                .withArguments(new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                        handler.getAllZonesNames("").toArray(new String[0]))))
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    Zone changeZone = handler.getZoneByName(zoneName);
                    if (changeZone == null) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
                        return;
                    }
                    Location newP1 = handler.pos1.get(player.getUniqueId());
                    Location newP2 = handler.pos2.get(player.getUniqueId());
                    if (newP1 == null || newP2 == null || !newP1.getWorld().equals(newP2.getWorld())) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                        return;
                    }
                    changeZone.min = newP1;
                    changeZone.max = newP2;
                    handler.saveZones();
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Границы зоны " + zoneName + " обновлены!");
                });

        // Subcommand: reload
        CommandAPICommand reload = new CommandAPICommand("reload")
                .withShortDescription("Перезагружает конфигурацию зон")
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    if (handler.reloadZonesFromConfig()) {
                        LegacyTextHandler.sendFormattedMessage(player, "<green>Конфигурация загружена успешно!");
                    } else {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Конфигурация не была загружена!");
                    }
                });

        // Register the main command with all subcommands
        zoneCommand.withSubcommands(pos1, pos2, create, toggle, remove, change, reload).register();
    }
}