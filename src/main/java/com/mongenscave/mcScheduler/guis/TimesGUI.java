package com.mongenscave.mcScheduler.guis;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.listener.Time;
import com.mongenscave.mcScheduler.util.ChatUtil;
import com.mongenscave.mcScheduler.util.ConfigUtil;
import com.mongenscave.mcScheduler.util.CronUtil;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongenscave.mcScheduler.listener.CommandCreateListener.playerCreateCommand;
import static com.mongenscave.mcScheduler.listener.CommandEditListener.playerEditCommand;
import static com.mongenscave.mcScheduler.listener.EventsCreateListener.playerChat;
import static com.mongenscave.mcScheduler.listener.TimeCreateListener.playerCreateTime;
import static com.mongenscave.mcScheduler.listener.TimeEditListener.playerEditTime;
import static com.mongenscave.mcScheduler.util.CronUtil.getEventTimes;

public class TimesGUI {

    public void openMenu(Player player, String eventName) {

        playerCreateCommand.remove(player);
        playerEditCommand.remove(player);
        playerCreateTime.remove(player);
        playerEditTime.remove(player);
        playerChat.remove(player);

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("times");
        List<String> times = getEventTimes(eventName);

        String rawTitle = guiConfig.getString("title").replace("%event_name%", eventName);
        String title;

        int size = guiConfig.getInt("size", 27);

        int rows = size / 9;
        int maxItemsPerPage = (rows - 1) * 9;
        int totalPages = (int) Math.ceil((double) times.size() / maxItemsPerPage);

        title = getFormattedTitle(rawTitle, 1, totalPages);

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(title))
                .rows(size / 9)
                .disableAllInteractions()
                .create();

        for (String time : times) {
            gui.addItem(createEventItem(time, eventName));
        }

        addDecorativeItems(gui);
        addCreateTimeButton(gui, eventName);
        addNavigationButtons(gui, rawTitle);
        addBackButton(gui, eventName);

        updateGuiTitle(gui, rawTitle);
        gui.open(player);
    }

    private GuiItem createEventItem(String time, String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("times");

        String path = "template-item";
        Material material = Material.matchMaterial(guiConfig.getString(path + ".material"));
        if (material == null) material = Material.CLOCK;

        String displayName = ChatUtil.colorizeHex(guiConfig.getString(path + ".name")
                .replace("%event_name%", event)
                .replace("%time%", time)
                .replace("%time_next_run%", CronUtil.getNextRunFromCron(time))
                .replace("%next_run%", CronUtil.getNextRun(event))
                .replace("%times_count%", String.valueOf(CronUtil.getEventTimes(event).size()))
                .replace("%commands_count%", String.valueOf(CronUtil.getEventCommands(event).size())));
        List<String> lore = guiConfig.getStringList(path + ".lore").stream()
                .map(line -> line
                        .replace("%event_name%", event)
                        .replace("%time%", time)
                        .replace("%time_next_run%", CronUtil.getNextRunFromCron(time))
                        .replace("%next_run%", CronUtil.getNextRun(event))
                        .replace("%times_count%", String.valueOf(CronUtil.getEventTimes(event).size()))
                        .replace("%commands_count%", String.valueOf(CronUtil.getEventCommands(event).size()))
                )
                .toList();

        ItemStack itemStack = new ItemStack(material);
        itemStack.editMeta(meta -> {
            meta.setDisplayName(displayName);
            meta.setLore(ChatUtil.colorizeHex(lore));
        });

        return new GuiItem(itemStack, guiEvent -> {
            guiEvent.setCancelled(true);

            if(guiEvent.getClick() == ClickType.DROP) {
                deleteTime(event, time);
                guiEvent.getWhoClicked().closeInventory();
                guiEvent.getWhoClicked().sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_deleted")
                        .replace("%event_name%", event))
                        .replace("%time%", time));
                openMenu((Player) guiEvent.getWhoClicked(), event);
                return;
            }

            guiEvent.getWhoClicked().sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_rename")));
            playerEditTime.put((Player) guiEvent.getWhoClicked(), new Time(time, event));
            guiEvent.getWhoClicked().closeInventory();
        });
    }

    private void addCreateTimeButton(PaginatedGui gui, String eventName) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("times");

        String createName = ChatUtil.colorizeHex(guiConfig.getString("create-item.name"));
        Material material = Material.matchMaterial(guiConfig.getString( "create-item.material"));
        if (material == null) material = Material.WRITABLE_BOOK;

        ItemStack createItem = new ItemStack(material);
        createItem.editMeta(meta -> meta.setDisplayName(createName));

        GuiItem createButton = new GuiItem(createItem, event -> {
            event.setCancelled(true);
            playerCreateTime.put((Player) event.getWhoClicked(), eventName);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_create")));
        });

        gui.setItem(guiConfig.getInt("create-item.slot"), createButton);
    }

    private void addNavigationButtons(PaginatedGui gui, String rawTitle) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("times");

        String nextName = ChatUtil.colorizeHex(guiConfig.getString("navigation.next-page.name"));
        String prevName = ChatUtil.colorizeHex(guiConfig.getString("navigation.previous-page.name"));

        ItemStack nextItem = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack prevItem = new ItemStack(Material.ARROW);

        nextItem.editMeta(meta -> meta.setDisplayName(nextName));
        prevItem.editMeta(meta -> meta.setDisplayName(prevName));

        List<Integer> nextSlots = guiConfig.getIntList("navigation.next-page.slot");
        List<Integer> prevSlots = guiConfig.getIntList("navigation.previous-page.slot");

        GuiItem nextButton = new GuiItem(nextItem, event -> {
            event.setCancelled(true);
            gui.next();
            updateGuiTitle(gui, rawTitle);
        });

        GuiItem prevButton = new GuiItem(prevItem, event -> {
            event.setCancelled(true);
            gui.previous();
            updateGuiTitle(gui, rawTitle);
        });

        nextSlots.forEach(slot -> gui.setItem(slot, nextButton));
        prevSlots.forEach(slot -> gui.setItem(slot, prevButton));
    }

    private void addDecorativeItems(PaginatedGui gui) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("times");

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

    private void addBackButton(PaginatedGui gui, String eventName) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var guiConfig = configUtil.getGui("commands");

        String itemName = ChatUtil.colorizeHex(guiConfig.getString("back-item.name"));
        Material material = Material.matchMaterial(guiConfig.getString( "back-item.material"));
        if (material == null) material = Material.BARRIER;

        ItemStack backItem = new ItemStack(material);
        backItem.editMeta(meta -> meta.setDisplayName(itemName));

        GuiItem backButton = new GuiItem(backItem, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            EditorGUI editorGUI = new EditorGUI();
            editorGUI.openMenu((Player) event.getWhoClicked(), eventName);
        });

        gui.setItem(guiConfig.getInt("back-item.slot"), backButton);
    }

    private String getFormattedTitle(String rawTitle, int currentPage, int totalPages) {
        return ChatUtil.colorizeHex(rawTitle
                .replace("%current_page%", String.valueOf(currentPage))
                .replace("%total_pages%", String.valueOf(totalPages)));
    }

    private void updateGuiTitle(PaginatedGui gui, String rawTitle) {
        String formattedTitle = getFormattedTitle(rawTitle, gui.getCurrentPageNum(), gui.getPagesNum());
        gui.updateTitle(formattedTitle);
    }

    private void deleteTime(String event, String time) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);

        if(eventSection == null) return;

        List<String> times = eventSection.getStringList("times");

        if(!times.contains(time)) return;

        if(times.isEmpty()) return;

        times.remove(time);

        eventSection.set("times", times);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}