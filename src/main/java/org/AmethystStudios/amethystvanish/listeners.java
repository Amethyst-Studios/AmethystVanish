package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final AmethystVanish plugin;

    public ChatListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Optional: Add vanish prefix to chat messages
        if (plugin.getVanishManager().isVanished(player)) {
            String format = event.getFormat();
            if (plugin.getConfigManager().getConfig().getBoolean("chat.vanish-prefix", true)) {
                event.setFormat("[V] " + format);
            }
        }
    }
}
