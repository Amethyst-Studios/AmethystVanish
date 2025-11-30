package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickListener implements Listener {

    private final AmethystVanish plugin;

    public InventoryClickListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = event.getView().getTitle();
        String guiTitle = plugin.getGuiManager().getGUITitle();

        // Check if this is our vanish GUI
        if (inventoryTitle.equals(guiTitle)) {
            event.setCancelled(true); // Prevent taking items

            if (event.getCurrentItem() == null) return;

            // Handle the click
            plugin.getGuiManager().handleGUIClick(player, event.getRawSlot());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // You can add any cleanup logic here if needed
    }
}
