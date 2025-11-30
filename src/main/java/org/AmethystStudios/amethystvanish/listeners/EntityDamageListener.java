package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private final AmethystVanish plugin;

    public EntityDamageListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.getVanishManager().isVanished(player)) {
                // Optional: Cancel damage while vanished
                if (plugin.getConfigManager().getConfig().getBoolean("vanish-effects.invulnerable", true)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
