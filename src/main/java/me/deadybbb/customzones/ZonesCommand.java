package me.deadybbb.customzones;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.deadybbb.customzones.ybbbbasicmodule.LegacyTextHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("zone")
@CommandPermission("customzones.zone")
public class ZonesCommand extends BaseCommand {
    private final ZonesHandler zonesHandler;

    public ZonesCommand(ZonesHandler zonesHandler) {
        this.zonesHandler = zonesHandler;
    }

    @Default
    @Description("Показывает использование команды /zone")
    public void onDefault(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            LegacyTextHandler.sendFormattedMessage(sender, "<red>Эта команда только для игроков!");
            return;
        }
        LegacyTextHandler.sendFormattedMessage(player, "<red>Использование: /zone <create|pos1|pos2|toggle|remove|change|reload> [name]");
    }

    @Subcommand("pos1")
    @Description("Устанавливает первую точку зоны")
    public void onPos1(Player player) {
        zonesHandler.pos1.put(player.getUniqueId(), player.getLocation());
        LegacyTextHandler.sendFormattedMessage(player, "<green>Первая точка установлена: " + LegacyTextHandler.formatLocation(player.getLocation()));
    }

    @Subcommand("pos2")
    @Description("Устанавливает вторую точку зоны")
    public void onPos2(Player player) {
        zonesHandler.pos2.put(player.getUniqueId(), player.getLocation());
        LegacyTextHandler.sendFormattedMessage(player, "<green>Вторая точка установлена: " + LegacyTextHandler.formatLocation(player.getLocation()));
    }

    @Subcommand("create")
    @Description("Создает новую зону")
    @CommandCompletion("@nothing")
    public void onCreate(Player player, String zoneName) {
        if (zonesHandler.getZoneByName(zoneName) != null) {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Зона с именем " + zoneName + " уже существует!");
            return;
        }
        Location p1 = zonesHandler.pos1.get(player.getUniqueId());
        Location p2 = zonesHandler.pos2.get(player.getUniqueId());
        if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
            return;
        }
        zonesHandler.zones.add(new Zone(zoneName, p1, p2, false));
        zonesHandler.saveZones();
        LegacyTextHandler.sendFormattedMessage(player, "<green>Зона " + zoneName + " создана!");
    }

    @Subcommand("toggle")
    @Description("Включает или выключает отображение зоны")
    @CommandCompletion("@zones")
    public void onToggle(Player player, String zoneName) {
        Zone toggleZone = zonesHandler.getZoneByName(zoneName);
        if (toggleZone == null) {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
            return;
        }
        toggleZone.displayEnabled = !toggleZone.displayEnabled;
        zonesHandler.saveZones();
        LegacyTextHandler.sendFormattedMessage(player, "<green>Отображение зоны " + zoneName + " " + (toggleZone.displayEnabled ? "включено" : "выключено"));
    }

    @Subcommand("remove")
    @Description("Удаляет зону")
    @CommandCompletion("@zones")
    public void onRemove(Player player, String zoneName) {
        Zone removeZone = zonesHandler.getZoneByName(zoneName);
        if (removeZone != null) {
            zonesHandler.zones.remove(removeZone);
            zonesHandler.saveZones();
            LegacyTextHandler.sendFormattedMessage(player, "<green>Зона " + zoneName + " удалена!");
        } else {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
        }
    }

    @Subcommand("change")
    @Description("Изменяет границы зоны")
    @CommandCompletion("@zones")
    public void onChange(Player player, String zoneName) {
        Zone changeZone = zonesHandler.getZoneByName(zoneName);
        if (changeZone == null) {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Зона " + zoneName + " не найдена!");
            return;
        }
        Location newP1 = zonesHandler.pos1.get(player.getUniqueId());
        Location newP2 = zonesHandler.pos2.get(player.getUniqueId());
        if (newP1 == null || newP2 == null || !newP1.getWorld().equals(newP2.getWorld())) {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
            return;
        }
        changeZone.min = newP1;
        changeZone.max = newP2;
        zonesHandler.saveZones();
        LegacyTextHandler.sendFormattedMessage(player, "<green>Границы зоны " + zoneName + " обновлены!");
    }

    @Subcommand("reload")
    @Description("Перезагружает конфигурацию зон")
    public void onReload(Player player) {
        if (zonesHandler.reloadZonesFromConfig()) {
            LegacyTextHandler.sendFormattedMessage(player, "<green>Конфигурация загружена успешно!");
        } else {
            LegacyTextHandler.sendFormattedMessage(player, "<red>Конфигурация не была загружена!");
        }
    }

    @CommandCompletion("@zones")
    public List<String> completeZones(Player player, String input) {
        return zonesHandler.getAllZonesNames(input);
    }
}