package com.mongenscave.mcScheduler.guis;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.util.ChatUtil;
import com.mongenscave.mcScheduler.util.ConfigUtil;
import com.mongenscave.mcScheduler.util.CronUtil;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static com.mongenscave.mcScheduler.listener.CommandCreateListener.playerCreateCommand;
import static com.mongenscave.mcScheduler.listener.CommandEditListener.playerEditCommand;
import static com.mongenscave.mcScheduler.listener.EventsCreateListener.playerChat;
import static com.mongenscave.mcScheduler.listener.TimeCreateListener.playerCreateTime;
import static com.mongenscave.mcScheduler.listener.TimeEditListener.playerEditTime;
import static com.mongenscave.mcScheduler.util.CronUtil.*;

public class EditorGUI {

    public void openMenu(Player player, String eventName) {

        playerCreateCommand.remove(player);
        playerEditCommand.remove(player);
        playerCreateTime.remove(player);
        playerEditTime.remove(player);
        playerChat.remove(player);

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("editor");

        String title = ChatUtil.colorizeHex(guiConfig.getString("title").replace("%event_name%", eventName));

        int size = guiConfig.getInt("size", 27);

        Gui gui = Gui.gui()
                .title(Component.text(title))
                .rows(size / 9)
                .disableAllInteractions()
                .create();


        addDecorativeItems(gui);
        addTimesButton(gui, eventName);
        addCommandsButton(gui, eventName);
        addBackButton(gui);

        gui.open(player);
    }

    private void addTimesButton(Gui gui, String eventName) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("editor");
        var items = guiConfig.getSection("items");

        String timesName = ChatUtil.colorizeHex(items.getString("times.name"));
        List<String> lore = items.getStringList("times.lore").stream()
                .map(ChatUtil::colorizeHex)
                .map(line -> line
                        .replace("%event_name%", eventName)
                        .replace("%next_run%", CronUtil.getNextRun(eventName))
                        .replace("%times_count%", String.valueOf(getEventTimes(eventName).size()))
                        .replace("%commands_count%", String.valueOf(getEventCommands(eventName).size())))
                .toList();

        Material material = Material.matchMaterial(items.getString( "times.material"));
        if (material == null) material = Material.CLOCK;

        ItemStack timesItem = new ItemStack(material);
        timesItem.editMeta(meta -> {
            meta.setDisplayName(timesName);
            meta.setLore(lore);
        });

        GuiItem timesButton = new GuiItem(timesItem, event -> {
            event.setCancelled(true);
            TimesGUI timesGUI = new TimesGUI();
            timesGUI.openMenu((Player) event.getWhoClicked(), eventName);
        });

        gui.setItem(items.getInt("times.slot"), timesButton);
    }

    private void addCommandsButton(Gui gui, String eventName) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("editor");
        var items = guiConfig.getSection("items");

        String commandsName = ChatUtil.colorizeHex(items.getString("commands.name"));
        List<String> lore = items.getStringList("commands.lore").stream()
                .map(ChatUtil::colorizeHex)
                .map(line -> line
                        .replace("%event_name%", eventName)
                        .replace("%next_run%", CronUtil.getNextRun(eventName))
                        .replace("%times_count%", String.valueOf(getEventTimes(eventName).size()))
                        .replace("%commands_count%", String.valueOf(getEventCommands(eventName).size())))
                .toList();

        Material material = Material.matchMaterial(items.getString( "commands.material"));
        if (material == null) material = Material.CLOCK;

        ItemStack commandsItem = new ItemStack(material);
        commandsItem.editMeta(meta -> {
            meta.setDisplayName(commandsName);
            meta.setLore(lore);
        });

        GuiItem timesButton = new GuiItem(commandsItem, event -> {
            event.setCancelled(true);
            CommandsGUI commandsGUI = new CommandsGUI();
            commandsGUI.openMenu((Player) event.getWhoClicked(), eventName);
        });

        gui.setItem(items.getInt("commands.slot"), timesButton);
    }



    private void addDecorativeItems(Gui gui) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("editor");

        var decorations = guiConfig.getSection("decoration");
        if (decorations != null) {
            for (String key : decorations.getRoutesAsStrings(false)) {
                var decoration = decorations.getSection(key);

                String displayItemStr = decoration.getString("material", "GRAY_STAINED_GLASS_PANE");
                Material displayItem;
                try {
                    displayItem = Material.valueOf(displayItemStr);
                } catch (IllegalArgumentException e) {
                    displayItem = Material.BEDROCK;
                }

                String displayName = ChatUtil.colorizeHex(decoration.getString("name", "&r"));
                List<String> lore = decoration.getStringList("lore").stream()
                        .map(ChatUtil::colorizeHex)
                        .collect(Collectors.toList());
                List<Integer> slots = decoration.getIntList("slot");

                ItemStack decorativeItem = new ItemStack(displayItem);
                decorativeItem.editMeta(meta -> {
                    meta.setDisplayName(displayName);
                    meta.setLore(lore);
                });

                GuiItem guiItem = new GuiItem(decorativeItem, event -> event.setCancelled(true));

                for (int slot : slots) {
                    if (slot >= 0 && slot < gui.getRows() * 9) {
                        gui.setItem(slot, guiItem);
                    }
                }
            }
        }
    }

    private void addBackButton(Gui gui) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("editor");

        String itemName = ChatUtil.colorizeHex(guiConfig.getString("back-item.name"));
        Material material = Material.matchMaterial(guiConfig.getString( "back-item.material"));
        if (material == null) material = Material.BARRIER;

        ItemStack backItem = new ItemStack(material);
        backItem.editMeta(meta -> meta.setDisplayName(itemName));

        GuiItem backButton = new GuiItem(backItem, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            EventsGUI eventsGUI = new EventsGUI();
            eventsGUI.openMenu((Player) event.getWhoClicked());
        });

        gui.setItem(guiConfig.getInt("back-item.slot"), backButton);
    }

}