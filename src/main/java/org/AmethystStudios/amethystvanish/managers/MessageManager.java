package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final AmethystVanish plugin;
    private final Map<String, String> messages;

    public MessageManager(AmethystVanish plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
    }

    public void loadMessages() {
        messages.clear();
        FileConfiguration messagesConfig = plugin.getConfigManager().getMessages();

        for (String key : messagesConfig.getKeys(true)) {
            if (messagesConfig.isString(key)) {
                messages.put(key, colorize(messagesConfig.getString(key)));
            }
        }
    }

    public String getMessage(String path) {
        return messages.getOrDefault(path, "&cMessage not found: " + path);
    }

    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public void sendMessage(Player player, String path) {
        player.sendMessage(getMessage(path));
    }

    public void sendMessage(Player player, String path, Map<String, String> placeholders) {
        player.sendMessage(getMessage(path, placeholders));
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
