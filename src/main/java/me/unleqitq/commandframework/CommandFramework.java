package me.unleqitq.commandframework;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandFramework extends JavaPlugin {
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		Bukkit.getPluginCommand("cfhelp").setExecutor((sender, command, label, args) -> {
			try {
				CommandManager manager = CommandManager.registeredManagers.get(args[0]);
				CommandNode node = manager.getRootNodes().get(args[1]);
				int i = 2;
				while (i < args.length) {
					node = node.getChild(args[i++]);
				}
				node.printPaperUsage(sender);
				return true;
			} catch (Exception ignored) {
				return false;
			}
		});
	}
	
	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
	
	public static boolean isVanished(Player player) {
		if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled(
				"PremiumVanish")) {
			if (VanishAPI.isInvisible(player))
				return true;
		}
		return player.isInvisible();
	}
	
	public static boolean isVanished(Player viewer, Player player) {
		if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled(
				"PremiumVanish")) {
			if (VanishAPI.isInvisible(player))
				return !VanishAPI.canSee(viewer, player);
		}
		return !viewer.canSee(player);
	}
	
}
