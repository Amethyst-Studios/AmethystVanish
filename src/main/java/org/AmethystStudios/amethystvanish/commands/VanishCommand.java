package org.AmethystStudios.amethystvanish.commands;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.AmethystStudios.amethystvanish.managers.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {

    private final AmethystVanish plugin;

    public VanishCommand(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("amethystvanish.use")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            // Toggle vanish
            if (plugin.getVanishManager().isVanished(player)) {
                plugin.getVanishManager().disableVanish(player, false);
            } else {
                plugin.getVanishManager().enableVanish(player, false);
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "gui":
                if (!player.hasPermission("amethystvanish.gui")) {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                    return true;
                }
                plugin.getGuiManager().openVanishGUI(player);
                break;

            case "silentchest":
            case "sc":
                if (!player.hasPermission("amethystvanish.silentchest")) {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                    return true;
                }
                plugin.getVanishManager().toggleSilentChestView(player);
                String silentChestStatus = plugin.getVanishManager().hasSilentChestView(player) ? "enabled" : "disabled";
                plugin.getMessageManager().sendMessage(player, "silent-chest." + silentChestStatus);
                break;

            case "nopickup":
            case "np":
                if (!player.hasPermission("amethystvanish.nopickup")) {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                    return true;
                }
                plugin.getVanishManager().toggleNoPickup(player);
                String noPickupStatus = plugin.getVanishManager().hasNoPickup(player) ? "enabled" : "disabled";
                plugin.getMessageManager().sendMessage(player, "no-pickup." + noPickupStatus);
                break;

            case "nointeract":
            case "ni":
                if (!player.hasPermission("amethystvanish.nointeract")) {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                    return true;
                }
                plugin.getVanishManager().toggleNoInteract(player);
                String noInteractStatus = plugin.getVanishManager().hasNoInteract(player) ? "enabled" : "disabled";
                plugin.getMessageManager().sendMessage(player, "no-interact." + noInteractStatus);
                break;

            case "reload":
                if (!player.hasPermission("amethystvanish.reload")) {
                    plugin.getMessageManager().sendMessage(player, "no-permission");
                    return true;
                }
                plugin.getConfigManager().reloadConfigs();
                plugin.getMessageManager().loadMessages();
                plugin.getGuiManager().loadGUI();
                plugin.getMessageManager().sendMessage(player, "config-reloaded");
                break;

            default:
                plugin.getMessageManager().sendMessage(player, "command-usage");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            if (sender.hasPermission("amethystvanish.gui") && "gui".startsWith(partial)) completions.add("gui");
            if (sender.hasPermission("amethystvanish.silentchest") && "silentchest".startsWith(partial)) completions.add("silentchest");
            if (sender.hasPermission("amethystvanish.nopickup") && "nopickup".startsWith(partial)) completions.add("nopickup");
            if (sender.hasPermission("amethystvanish.nointeract") && "nointeract".startsWith(partial)) completions.add("nointeract");
            if (sender.hasPermission("amethystvanish.reload") && "reload".startsWith(partial)) completions.add("reload");

            // Add short versions
            if (sender.hasPermission("amethystvanish.silentchest") && "sc".startsWith(partial)) completions.add("sc");
            if (sender.hasPermission("amethystvanish.nopickup") && "np".startsWith(partial)) completions.add("np");
            if (sender.hasPermission("amethystvanish.nointeract") && "ni".startsWith(partial)) completions.add("ni");
        }

        return completions;
    }
}
