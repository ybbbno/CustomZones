package me.deadybbb.customzones;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import me.deadybbb.customzones.prefixes.PrefixHandler;
import me.deadybbb.customzones.zone.Zone;
import me.deadybbb.customzones.zone.ZoneManager;
import me.deadybbb.ybmj.LegacyTextHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class CustomZonesCommand {
    private final CustomZones plugin;
    private final ZoneManager handler;
    private final PrefixHandler prefixHandler;

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    public CustomZonesCommand(CustomZones plugin, ZoneManager handler, PrefixHandler prefixHandler) {
        this.plugin = plugin;
        this.handler = handler;
        this.prefixHandler = prefixHandler;
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
                    this.pos1.put(player.getUniqueId(), player.getLocation());
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Первая точка установлена: " + LegacyTextHandler.formatLocation(player.getLocation()));
                });

        // Subcommand: pos2
        CommandAPICommand pos2 = new CommandAPICommand("pos2")
                .withShortDescription("Устанавливает вторую точку зоны")
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    this.pos2.put(player.getUniqueId(), player.getLocation());
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
                    Location p1 = this.pos1.get(player.getUniqueId());
                    Location p2 = this.pos2.get(player.getUniqueId());
                    if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                        return;
                    }

                    if (handler.addZone(zoneName, p1, p2, new ArrayList<>())) {
                        LegacyTextHandler.sendFormattedMessage(player, "<green>Зона " + zoneName + " создана!");
                    } else {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не была создана!");
                    }
                });

        // Subcommand: remove
        CommandAPICommand remove = new CommandAPICommand("remove")
                .withShortDescription("Удаляет зону")
                .withArguments(new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                        handler.getAllZonesNames("").toArray(new String[0]))))
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    if (handler.removeZone(zoneName)) {
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
                    Location newP1 = this.pos1.get(player.getUniqueId());
                    Location newP2 = this.pos2.get(player.getUniqueId());
                    if (newP1 == null || newP2 == null || !newP1.getWorld().equals(newP2.getWorld())) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                        return;
                    }
                    changeZone.min = newP1;
                    changeZone.max = newP2;
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Границы зоны " + zoneName + " обновлены!");
                });

        // Subcommand: addprefix
        CommandAPICommand addPrefix = new CommandAPICommand("addprefix")
                .withShortDescription("Добавляет префикс к зоне")
                .withArguments(
                        new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                                handler.getAllZonesNames("").toArray(new String[0]))),
                        new StringArgument("prefix").replaceSuggestions(ArgumentSuggestions.strings(info ->
                                prefixHandler.getPrefixes().toArray(new String[0])))
                )
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    String prefix = (String) args.get("prefix");
                    Zone zone = handler.getZoneByName(zoneName);
                    if (zone == null) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
                        return;
                    }
                    zone.addPrefix(prefix);
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Префикс " + prefix + " добавлен к зоне " + zoneName);
                });

        // Subcommand: removeprefix
        CommandAPICommand removePrefix = new CommandAPICommand("removeprefix")
                .withShortDescription("Удаляет префикс из зоны")
                .withArguments(
                        new StringArgument("zoneName").replaceSuggestions(ArgumentSuggestions.strings(info ->
                                handler.getAllZonesNames("").toArray(new String[0]))),
                        new StringArgument("prefix").replaceSuggestions(ArgumentSuggestions.strings(info ->
                                prefixHandler.getPrefixes().toArray(new String[0])))
                )
                .executesPlayer((PlayerCommandExecutor) (player, args) -> {
                    String zoneName = (String) args.get("zoneName");
                    String prefix = (String) args.get("prefix");
                    Zone zone = handler.getZoneByName(zoneName);
                    if (zone == null) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
                        return;
                    }
                    if (!zone.hasPrefix(prefix)) {
                        LegacyTextHandler.sendFormattedMessage(player, "<red>Префикс " + prefix + " не найден в зоне " + zoneName);
                        return;
                    }
                    zone.removePrefix(prefix);
                    LegacyTextHandler.sendFormattedMessage(player, "<green>Префикс " + prefix + " удалён из зоны " + zoneName);
                });

        // Register the main command with all subcommands
        zoneCommand.withSubcommands(pos1, pos2, create, remove, change, addPrefix, removePrefix).register();
    }
}