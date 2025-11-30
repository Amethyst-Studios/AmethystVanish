package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager {

    private final AmethystVanish plugin;
    private FileConfiguration guiConfig;
    private Map<String, ItemStack> guiItems;
    private String guiTitle;
    private int guiRows;

    public GUIManager(AmethystVanish plugin) {
        this.plugin = plugin;
        this.guiItems = new HashMap<>();
    }

    public void loadGUI() {
        guiConfig = plugin.getConfigManager().getGUI();
        guiTitle = colorize(guiConfig.getString("title", "&5Amethyst Vanish"));
        guiRows = guiConfig.getInt("rows", 3);

        loadGUIItems();
    }

    public String getGUITitle() {
        return guiTitle;
    }

    private void loadGUIItems() {
        guiItems.clear();

        // Main vanish toggle item
        guiItems.put("main-vanish", createGUIItem(
                Material.valueOf(guiConfig.getString("items.main-vanish.material", "ENDER_EYE")),
                guiConfig.getString("items.main-vanish.name", "&aToggle Vanish"),
                guiConfig.getStringList("items.main-vanish.lore"),
                guiConfig.getInt("items.main-vanish.slot", 11)
        ));

        // Silent chest item
        guiItems.put("silent-chest", createGUIItem(
                Material.valueOf(guiConfig.getString("items.silent-chest.material", "CHEST")),
                guiConfig.getString("items.silent-chest.name", "&eSilent Chest View"),
                guiConfig.getStringList("items.silent-chest.lore"),
                guiConfig.getInt("items.silent-chest.slot", 13)
        ));

        // No pickup item
        guiItems.put("no-pickup", createGUIItem(
                Material.valueOf(guiConfig.getString("items.no-pickup.material", "HOPPER")),
                guiConfig.getString("items.no-pickup.name", "&cNo Item Pickup"),
                guiConfig.getStringList("items.no-pickup.lore"),
                guiConfig.getInt("items.no-pickup.slot", 15)
        ));

        // No interact item
        guiItems.put("no-interact", createGUIItem(
                Material.valueOf(guiConfig.getString("items.no-interact.material", "BARRIER")),
                guiConfig.getString("items.no-interact.name", "&6No Interaction"),
                guiConfig.getStringList("items.no-interact.lore"),
                guiConfig.getInt("items.no-interact.slot", 17)
        ));

        // Close button
        guiItems.put("close", createGUIItem(
                Material.valueOf(guiConfig.getString("items.close.material", "REDSTONE")),
                guiConfig.getString("items.close.name", "&cClose"),
                guiConfig.getStringList("items.close.lore"),
                guiConfig.getInt("items.close.slot", 26)
        ));
    }

    private ItemStack createGUIItem(Material material, String name, List<String> lore, int slot) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(colorize(name));

            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(colorize(line));
            }
            meta.setLore(coloredLore);

            item.setItemMeta(meta);
        }

        return item;
    }

    public void openVanishGUI(Player player) {
        if (!plugin.getConfigManager().getConfig().getBoolean("gui.enabled", true)) {
            plugin.getMessageManager().sendMessage(player, "gui-disabled");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, guiRows * 9, guiTitle);

        // Add all items to GUI with dynamic status
        for (Map.Entry<String, ItemStack> entry : guiItems.entrySet()) {
            ItemStack item = entry.getValue().clone();
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    List<String> updatedLore = new ArrayList<>();
                    for (String line : lore) {
                        String updatedLine = replacePlaceholders(player, line);
                        updatedLore.add(updatedLine);
                    }
                    meta.setLore(updatedLore);
                }
                item.setItemMeta(meta);
            }

            int slot = getSlotForItem(entry.getKey());
            if (slot >= 0 && slot < gui.getSize()) {
                gui.setItem(slot, item);
            }
        }

        // Fill empty slots with glass panes
        ItemStack filler = createFillerItem();
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    private String replacePlaceholders(Player player, String text) {
        Map<String, String> placeholders = new HashMap<>();

        // Vanish status
        boolean isVanished = plugin.getVanishManager().isVanished(player);
        placeholders.put("vanish_status", isVanished ? "&aEnabled" : "&cDisabled");

        // Feature statuses
        placeholders.put("silent_chest_status", plugin.getVanishManager().hasSilentChestView(player) ? "&aEnabled" : "&cDisabled");
        placeholders.put("no_pickup_status", plugin.getVanishManager().hasNoPickup(player) ? "&aEnabled" : "&cDisabled");
        placeholders.put("no_interact_status", plugin.getVanishManager().hasNoInteract(player) ? "&aEnabled" : "&cDisabled");

        // Player placeholders
        placeholders.put("player", player.getName());
        placeholders.put("online_players", String.valueOf(Bukkit.getOnlinePlayers().size()));
        placeholders.put("vanished_players", String.valueOf(plugin.getVanishManager().getVanishedPlayers().size()));

        // Replace all placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return colorize(text);
    }

    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.valueOf(guiConfig.getString("filler.material", "GRAY_STAINED_GLASS_PANE")));
        ItemMeta meta = filler.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        return filler;
    }

    private int getSlotForItem(String itemKey) {
        return guiConfig.getInt("items." + itemKey + ".slot", 0);
    }

    public String getItemKeyFromSlot(int slot) {
        for (Map.Entry<String, ItemStack> entry : guiItems.entrySet()) {
            int itemSlot = getSlotForItem(entry.getKey());
            if (itemSlot == slot) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void handleGUIClick(Player player, int slot) {
        String itemKey = getItemKeyFromSlot(slot);

        if (itemKey == null) return;

        switch (itemKey) {
            case "main-vanish":
                if (plugin.getVanishManager().isVanished(player)) {
                    plugin.getVanishManager().disableVanish(player, false);
                } else {
                    plugin.getVanishManager().enableVanish(player, false);
                }
                break;

            case "silent-chest":
                if (player.hasPermission("amethystvanish.silentchest")) {
                    plugin.getVanishManager().toggleSilentChestView(player);
                    String status = plugin.getVanishManager().hasSilentChestView(player) ? "enabled" : "disabled";
                    plugin.getMessageManager().sendMessage(player, "silent-chest." + status);
                } else {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                }
                break;

            case "no-pickup":
                if (player.hasPermission("amethystvanish.nopickup")) {
                    plugin.getVanishManager().toggleNoPickup(player);
                    String status = plugin.getVanishManager().hasNoPickup(player) ? "enabled" : "disabled";
                    plugin.getMessageManager().sendMessage(player, "no-pickup." + status);
                } else {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                }
                break;

            case "no-interact":
                if (player.hasPermission("amethystvanish.nointeract")) {
                    plugin.getVanishManager().toggleNoInteract(player);
                    String status = plugin.getVanishManager().hasNoInteract(player) ? "enabled" : "disabled";
                    plugin.getMessageManager().sendMessage(player, "no-interact." + status);
                } else {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                }
                break;

            case "close":
                player.closeInventory();
                break;
        }

        // Update the GUI if not closing
        if (!itemKey.equals("close")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    openVanishGUI(player);
                }
            }, 1L);
        }
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void reloadGUI() {
        loadGUI();
    }
}
