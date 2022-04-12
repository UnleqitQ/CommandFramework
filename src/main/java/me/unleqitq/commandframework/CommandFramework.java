package me.unleqitq.commandframework;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandFramework extends JavaPlugin {
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		
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
